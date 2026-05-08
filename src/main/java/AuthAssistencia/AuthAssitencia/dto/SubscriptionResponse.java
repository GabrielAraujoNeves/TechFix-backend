package AuthAssistencia.AuthAssitencia.dto;

import AuthAssistencia.AuthAssitencia.Model.PaymentMethod;
import AuthAssistencia.AuthAssitencia.Model.PaymentPeriod;
import AuthAssistencia.AuthAssitencia.Model.PlanType;
import AuthAssistencia.AuthAssitencia.Model.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SubscriptionResponse {
    private Long id;
    private PlanType planType;
    private PaymentPeriod paymentPeriod;
    private SubscriptionStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String message;
}