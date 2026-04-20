package AuthAssistencia.AuthAssitencia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String role;
    private String message;

    // Construtor para compatibilidade
    public AuthResponse(String token, String message) {
        this.token = token;
        this.role = "USER";
        this.message = message;
    }
}