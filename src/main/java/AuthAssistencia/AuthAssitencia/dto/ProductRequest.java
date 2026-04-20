package AuthAssistencia.AuthAssitencia.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Modelo é obrigatório")
    @Size(min = 2, max = 50, message = "Modelo deve ter entre 2 e 50 caracteres")
    private String modelo;

    @NotBlank(message = "Marca é obrigatória")
    @Size(min = 2, max = 50, message = "Marca deve ter entre 2 e 50 caracteres")
    private String marca;

    @NotNull(message = "Preço atacado é obrigatório")
    @Positive(message = "Preço atacado deve ser positivo")
    private Double precoAtacado;

    @NotNull(message = "Preço conserto com peça é obrigatório")
    @Positive(message = "Preço conserto com peça deve ser positivo")
    private Double precoConsertoPeca;

    @NotNull(message = "Quantidade em estoque é obrigatória")
    @Min(value = 0, message = "Quantidade em estoque não pode ser negativa")
    @Max(value = 99999, message = "Quantidade em estoque não pode exceder 99999")
    private Integer quantidadeEstoque;

    private Integer estoqueMinimo;  // Opcional, padrão 5
}