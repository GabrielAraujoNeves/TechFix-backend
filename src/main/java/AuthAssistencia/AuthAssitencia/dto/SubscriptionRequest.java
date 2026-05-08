package AuthAssistencia.AuthAssitencia.dto;

import AuthAssistencia.AuthAssitencia.Model.PaymentMethod;
import AuthAssistencia.AuthAssitencia.Model.PaymentPeriod;
import AuthAssistencia.AuthAssitencia.Model.PlanType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriptionRequest {
    @NotNull private PlanType planType;
    @NotNull private PaymentPeriod period;
    @NotNull private PaymentMethod paymentMethod;
}