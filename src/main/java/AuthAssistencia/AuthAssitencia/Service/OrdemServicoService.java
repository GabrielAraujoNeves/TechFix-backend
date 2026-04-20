package AuthAssistencia.AuthAssitencia.Service;

import AuthAssistencia.AuthAssitencia.Model.*;
import AuthAssistencia.AuthAssitencia.Repository.*;
import AuthAssistencia.AuthAssitencia.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdemServicoService {

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private ItemOrdemServicoRepository itemOrdemServicoRepository;

    @Autowired
    private ProductRepository productRepository;

    private String gerarNumeroOS() {
        String ano = String.valueOf(LocalDateTime.now().getYear());
        String mes = String.format("%02d", LocalDateTime.now().getMonthValue());
        long count = ordemServicoRepository.count() + 1;
        String sequencia = String.format("%04d", count);
        return "OS-" + ano + mes + "-" + sequencia;
    }

    // Criar Ordem de Serviço (valores vêm do produto)
    @Transactional
    public OrdemServicoResponse createOrdemServico(OrdemServicoRequest request) {
        // Criar OS
        OrdemServico os = new OrdemServico();
        os.setNumeroOS(gerarNumeroOS());
        os.setClienteNome(request.getClienteNome());
        os.setClienteTelefone(request.getClienteTelefone());
        os.setClienteEmail(request.getClienteEmail());
        os.setModelo(request.getModelo());
        os.setMarca(request.getMarca());
        os.setProblema(request.getProblema());
        os.setObservacoes(request.getObservacoes());
        os.setStatus(StatusOrdem.PENDENTE);

        // Salvar OS para gerar ID
        OrdemServico savedOS = ordemServicoRepository.save(os);

        double totalPecas = 0.0;
        double totalConsertos = 0.0;

        // Adicionar itens (produtos do estoque)
        if (request.getItens() != null && !request.getItens().isEmpty()) {
            for (ItemOrdemRequest itemReq : request.getItens()) {
                Product produto = productRepository.findById(itemReq.getProdutoId())
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemReq.getProdutoId()));

                // Verificar estoque
                if (produto.getQuantidadeEstoque() < itemReq.getQuantidade()) {
                    throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome() +
                            ". Disponível: " + produto.getQuantidadeEstoque());
                }

                // ⭐ VALORES VÊM DO PRODUTO
                double precoPeca = produto.getPrecoAtacado();           // Preço da peça
                double valorConsertoItem = produto.getPrecoConsertoPeca(); // ⭐ VALOR DO CONSERTO DA PEÇA
                double valorTotalPeca = precoPeca * itemReq.getQuantidade();
                double subtotal = valorTotalPeca + valorConsertoItem;

                totalPecas += valorTotalPeca;
                totalConsertos += valorConsertoItem;

                // Criar item
                ItemOrdemServico item = new ItemOrdemServico();
                item.setOrdemServico(savedOS);
                item.setProduto(produto);
                item.setQuantidade(itemReq.getQuantidade());
                item.setPrecoUnitarioPeca(precoPeca);
                item.setValorTotalPeca(valorTotalPeca);
                item.setValorConserto(valorConsertoItem);
                item.setSubtotal(subtotal);
                item.setObservacao(itemReq.getObservacao());

                itemOrdemServicoRepository.save(item);

                // Dar baixa no estoque
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - itemReq.getQuantidade());
                productRepository.save(produto);
            }
        }

        // Atualizar totais
        os.setValorTotalConserto(totalConsertos);  // Soma dos consertos das peças
        os.setValorTotalPecas(totalPecas);
        os.setValorTotalGeral(totalPecas + totalConsertos);

        OrdemServico finalOS = ordemServicoRepository.save(savedOS);

        return convertToResponse(finalOS);
    }

    // Cancelar OS (devolve produtos ao estoque)
    @Transactional
    public OrdemServicoResponse cancelOrdem(Long id, String motivo) {
        OrdemServico os = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada: " + id));

        for (ItemOrdemServico item : os.getItens()) {
            Product produto = item.getProduto();
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade());
            productRepository.save(produto);
        }

        os.setStatus(StatusOrdem.CANCELADO);
        os.setObservacoes(os.getObservacoes() + " | OS Cancelada: " + motivo);

        return convertToResponse(ordemServicoRepository.save(os));
    }

    // Deletar OS
    @Transactional
    public void deleteOrdem(Long id) {
        OrdemServico os = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada: " + id));

        if (os.getStatus() != StatusOrdem.FINALIZADO && os.getStatus() != StatusOrdem.CANCELADO) {
            for (ItemOrdemServico item : os.getItens()) {
                Product produto = item.getProduto();
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade());
                productRepository.save(produto);
            }
        }

        ordemServicoRepository.delete(os);
    }

    // Atualizar status da OS
    @Transactional
    public OrdemServicoResponse updateStatus(Long id, AtualizarStatusRequest request) {
        OrdemServico os = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada: " + id));

        StatusOrdem novoStatus = StatusOrdem.valueOf(request.getStatus());
        os.setStatus(novoStatus);

        if (request.getObservacao() != null) {
            os.setObservacoes(os.getObservacoes() + " | Status alterado: " + novoStatus.getDescricao() + " - " + request.getObservacao());
        }

        return convertToResponse(ordemServicoRepository.save(os));
    }

    // Listar todas OS
    public List<OrdemServicoResponse> getAllOrdens() {
        return ordemServicoRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Buscar OS por ID
    public OrdemServicoResponse getOrdemById(Long id) {
        OrdemServico os = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada: " + id));
        return convertToResponse(os);
    }

    // Buscar por número OS
    public OrdemServicoResponse getOrdemByNumero(String numeroOS) {
        OrdemServico os = ordemServicoRepository.findByNumeroOS(numeroOS);
        if (os == null) {
            throw new RuntimeException("Ordem de Serviço não encontrada: " + numeroOS);
        }
        return convertToResponse(os);
    }

    // Buscar por status
    public List<OrdemServicoResponse> getOrdensByStatus(String status) {
        StatusOrdem statusEnum = StatusOrdem.valueOf(status);
        return ordemServicoRepository.findByStatus(statusEnum)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Buscar por cliente
    public List<OrdemServicoResponse> getOrdensByCliente(String nome) {
        return ordemServicoRepository.findByClienteNomeContainingIgnoreCase(nome)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Converter para Response
    private OrdemServicoResponse convertToResponse(OrdemServico os) {
        List<ItemOrdemResponse> itensResponse = os.getItens().stream()
                .map(item -> new ItemOrdemResponse(
                        item.getId(),
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getProduto().getModelo(),
                        item.getProduto().getMarca(),
                        item.getQuantidade(),
                        item.getPrecoUnitarioPeca(),
                        item.getValorTotalPeca(),
                        item.getValorConserto(),
                        item.getSubtotal(),
                        item.getObservacao()
                ))
                .collect(Collectors.toList());

        return new OrdemServicoResponse(
                os.getId(),
                os.getNumeroOS(),
                os.getClienteNome(),
                os.getClienteTelefone(),
                os.getClienteEmail(),
                os.getModelo(),
                os.getMarca(),
                os.getProblema(),
                os.getObservacoes(),
                os.getStatus().toString(),
                os.getValorTotalConserto(),
                os.getValorTotalPecas(),
                os.getValorTotalGeral(),
                itensResponse,
                os.getDataAbertura(),
                os.getDataFinalizacao(),
                os.getCreatedAt(),
                os.getUpdatedAt()
        );
    }
}