package AuthAssistencia.AuthAssitencia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemOrdemResponse {
    private Long id;
    private Long produtoId;
    private String produtoNome;
    private String produtoModelo;
    private String produtoMarca;
    private Integer quantidade;
    private Double precoUnitarioPeca;
    private Double valorTotalPeca;
    private Double valorConserto;
    private Double subtotal;
    private String observacao;
}