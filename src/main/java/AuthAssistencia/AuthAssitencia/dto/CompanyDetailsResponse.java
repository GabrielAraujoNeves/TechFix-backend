package AuthAssistencia.AuthAssitencia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CompanyDetailsResponse {
    private Long id;
    private String nomeEmpresa;
    private String cnpj;
    private String endereco;
    private String cidade;
    private String estado;
    private String telefoneComercial;
    private String segmentoAtuacao;
    private String segmentoCode;
    private String edificacaoId;
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}