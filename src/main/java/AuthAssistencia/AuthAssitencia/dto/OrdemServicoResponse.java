package AuthAssistencia.AuthAssitencia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class OrdemServicoResponse {
    private Long id;
    private String numeroOS;
    private String clienteNome;
    private String clienteTelefone;
    private String clienteEmail;
    private String modelo;
    private String marca;
    private String problema;
    private String observacoes;
    private String status;
    private Double valorTotalConserto;
    private Double valorTotalPecas;
    private Double valorTotalGeral;
    private List<ItemOrdemResponse> itens;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFinalizacao;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}