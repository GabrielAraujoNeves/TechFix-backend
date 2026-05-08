package AuthAssistencia.AuthAssitencia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CardInfo {

    @NotBlank
    private String cardNumber;      // número completo (será usado apenas para extrair last4 e brand)

    @NotBlank
    private String cardholderName;

    @NotBlank
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Formato MM/AA")
    private String expiryDate;      // ex: "12/28"

    @NotBlank
    @Pattern(regexp = "\\d{3,4}", message = "CVV com 3 ou 4 dígitos")
    private String cvv;             // não será armazenado, usado apenas para simular validação
}