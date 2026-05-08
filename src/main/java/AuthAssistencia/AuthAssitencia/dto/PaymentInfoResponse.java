package AuthAssistencia.AuthAssitencia.dto;

import AuthAssistencia.AuthAssitencia.Model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfoResponse {
    private PaymentMethod paymentMethod;      // PIX, CREDIT_CARD, DEBIT_CARD
    private CardDetails cardDetails;          // detalhes do cartão (se existir)

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardDetails {
        private String last4;
        private String brand;
        private Integer expiryMonth;
        private Integer expiryYear;
        private String cardholderName;
        private String cardToken; // opcional, para referência
    }
}