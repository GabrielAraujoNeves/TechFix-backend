package AuthAssistencia.AuthAssitencia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String nome;
    private String modelo;
    private String marca;
    private Double precoAtacado;
    private Double precoConsertoPeca;
    private Integer quantidadeEstoque;
    private Integer estoqueMinimo;
    private Boolean estoqueBaixo;  // NOVO: alerta de estoque baixo
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}