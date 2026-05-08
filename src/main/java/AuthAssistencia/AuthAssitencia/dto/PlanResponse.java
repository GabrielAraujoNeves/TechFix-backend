package AuthAssistencia.AuthAssitencia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlanResponse {
    private String name;
    private String description;
    private double monthlyPrice;
}