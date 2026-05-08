package AuthAssistencia.AuthAssitencia.dto;

import AuthAssistencia.AuthAssitencia.Model.PaymentMethod;
import AuthAssistencia.AuthAssitencia.Model.PaymentPeriod;
import AuthAssistencia.AuthAssitencia.Model.PlanType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {

    // Dados do usuário
    @NotBlank private String name;
    @NotBlank @Email private String email;
    @NotBlank private String password;
    private String role; // "ADMIN" ou "USER" (opcional)

    // Dados da empresa
    @NotBlank private String nomeEmpresa;
    @NotBlank private String cnpj;
    @NotBlank private String endereco;
    @NotBlank private String cidade;
    @NotBlank private String estado;
    @NotBlank private String telefoneComercial;
    @NotBlank private String segmentoAtuacao;   // descrição (ex: "Assistência de Celular")

    // Código do segmento (obrigatório) – ex: "CEL", "AC", "REFRIG"
    @NotBlank private String segmentoCode;

    // edificacaoId é gerado automaticamente, não precisa enviar no JSON
    // private String edificacaoId;  // remova do request ou deixe opcional

    // Dados do plano
    @NotNull private PlanType planType;
    @NotNull private PaymentPeriod paymentPeriod;
    @NotNull private PaymentMethod paymentMethod;

    private CardInfo cardInfo;
}