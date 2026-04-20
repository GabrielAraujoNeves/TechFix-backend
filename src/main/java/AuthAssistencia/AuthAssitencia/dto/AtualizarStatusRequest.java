package AuthAssistencia.AuthAssitencia.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AtualizarStatusRequest {

    @NotNull(message = "Status é obrigatório")
    private String status; // PENDENTE, CONSERTANDO, FINALIZADO, CANCELADO

    private String observacao;
}