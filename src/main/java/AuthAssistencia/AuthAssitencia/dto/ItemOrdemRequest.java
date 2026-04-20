package AuthAssistencia.AuthAssitencia.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemOrdemRequest {

    @NotNull(message = "ID do produto é obrigatório")
    private Long produtoId;

    @NotNull(message = "Quantidade é obrigatória")
    private Integer quantidade;

    private Double valorConsertoCustomizado; // Opcional: valor diferente do padrão

    private String observacao;
}