package AuthAssistencia.AuthAssitencia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private String message;
    private boolean success;
    private Long userId;
    private Long companyId;
    private Long subscriptionId;
}