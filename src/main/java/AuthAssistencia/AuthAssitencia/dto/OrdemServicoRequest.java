package AuthAssistencia.AuthAssitencia.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class OrdemServicoRequest {

    @NotBlank(message = "Nome do cliente é obrigatório")
    private String clienteNome;

    private String clienteTelefone;
    private String clienteEmail;

    @NotBlank(message = "Modelo é obrigatório")
    private String modelo;

    @NotBlank(message = "Marca é obrigatória")
    private String marca;

    @NotBlank(message = "Problema é obrigatório")
    private String problema;

    private String observacoes;

    // REMOVI o valorConserto daqui - ele vem do produto

    private List<ItemOrdemRequest> itens;
}