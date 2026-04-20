package AuthAssistencia.AuthAssitencia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyResponse {
    private boolean valid;
    private String email;
    private String role;  // NOVO: role do usuário
    private String message;
}