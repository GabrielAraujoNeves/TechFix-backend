package AuthAssistencia.AuthAssitencia.Service;

import AuthAssistencia.AuthAssitencia.Model.Product;
import AuthAssistencia.AuthAssitencia.Repository.ProductRepository;
import AuthAssistencia.AuthAssitencia.dto.ProductRequest;
import AuthAssistencia.AuthAssitencia.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Criar produto com verificação de duplicata
    public ProductResponse createProduct(ProductRequest request) {
        // Verificar se produto já existe
        if (productRepository.existsByNomeAndModeloAndMarca(
                request.getNome(),
                request.getModelo(),
                request.getMarca())) {
            throw new RuntimeException("Produto já cadastrado! Verifique nome, modelo e marca.");
        }

        Product product = new Product();
        product.setNome(request.getNome());
        product.setModelo(request.getModelo());
        product.setMarca(request.getMarca());
        product.setPrecoAtacado(request.getPrecoAtacado());
        product.setPrecoConsertoPeca(request.getPrecoConsertoPeca());
        product.setQuantidadeEstoque(request.getQuantidadeEstoque());

        if (request.getEstoqueMinimo() != null) {
            product.setEstoqueMinimo(request.getEstoqueMinimo());
        }

        Product saved = productRepository.save(product);
        return convertToResponse(saved);
    }

    // Atualizar produto com verificação
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));

        // Verificar se outro produto com mesmos dados já existe
        productRepository.findByNomeAndModeloAndMarca(
                        request.getNome(),
                        request.getModelo(),
                        request.getMarca())
                .ifPresent(existingProduct -> {
                    if (!existingProduct.getId().equals(id)) {
                        throw new RuntimeException("Já existe outro produto com mesmo nome, modelo e marca!");
                    }
                });

        product.setNome(request.getNome());
        product.setModelo(request.getModelo());
        product.setMarca(request.getMarca());
        product.setPrecoAtacado(request.getPrecoAtacado());
        product.setPrecoConsertoPeca(request.getPrecoConsertoPeca());
        product.setQuantidadeEstoque(request.getQuantidadeEstoque());

        if (request.getEstoqueMinimo() != null) {
            product.setEstoqueMinimo(request.getEstoqueMinimo());
        }

        Product updated = productRepository.save(product);
        return convertToResponse(updated);
    }

    // Adicionar estoque
    @Transactional
    public ProductResponse addStock(Long id, Integer quantidade) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));

        if (quantidade <= 0) {
            throw new RuntimeException("Quantidade para adicionar deve ser positiva");
        }

        product.setQuantidadeEstoque(product.getQuantidadeEstoque() + quantidade);
        Product updated = productRepository.save(product);
        return convertToResponse(updated);
    }

    // Remover estoque (venda)
    @Transactional
    public ProductResponse removeStock(Long id, Integer quantidade) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));

        if (quantidade <= 0) {
            throw new RuntimeException("Quantidade para remover deve ser positiva");
        }

        if (product.getQuantidadeEstoque() < quantidade) {
            throw new RuntimeException("Estoque insuficiente! Disponível: " + product.getQuantidadeEstoque());
        }

        product.setQuantidadeEstoque(product.getQuantidadeEstoque() - quantidade);
        Product updated = productRepository.save(product);
        return convertToResponse(updated);
    }

    // Listar produtos com estoque baixo
    public List<ProductResponse> getLowStockProducts() {
        return productRepository.findProductsWithLowStock()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Listar produtos sem estoque
    public List<ProductResponse> getOutOfStockProducts() {
        return productRepository.findProductsOutOfStock()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Listar todos produtos
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Buscar produto por ID
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));
        return convertToResponse(product);
    }

    // Buscar por marca
    public List<ProductResponse> getProductsByMarca(String marca) {
        return productRepository.findByMarca(marca)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Buscar por modelo
    public List<ProductResponse> getProductsByModelo(String modelo) {
        return productRepository.findByModelo(modelo)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Buscar por nome
    public List<ProductResponse> searchProductsByNome(String nome) {
        return productRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Deletar produto
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));
        productRepository.delete(product);
    }

    // Converter Entity para Response
    private ProductResponse convertToResponse(Product product) {
        boolean estoqueBaixo = product.getQuantidadeEstoque() <= product.getEstoqueMinimo();

        return new ProductResponse(
                product.getId(),
                product.getNome(),
                product.getModelo(),
                product.getMarca(),
                product.getPrecoAtacado(),
                product.getPrecoConsertoPeca(),
                product.getQuantidadeEstoque(),
                product.getEstoqueMinimo(),
                estoqueBaixo,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    // Buscar último produto cadastrado
    public ProductResponse getLastCreatedProduct() {
        Product product = productRepository.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("Nenhum produto encontrado"));
        return convertToResponse(product);
    }


    // Buscar último produto atualizado
    public ProductResponse getLastUpdatedProduct() {
        Product product = productRepository.findTopByOrderByUpdatedAtDesc()
                .orElseThrow(() -> new RuntimeException("Nenhum produto encontrado"));
        return convertToResponse(product);
    }
}