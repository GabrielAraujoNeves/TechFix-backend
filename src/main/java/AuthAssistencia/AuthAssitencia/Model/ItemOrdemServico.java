package AuthAssistencia.AuthAssitencia.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "itens_ordem_servico")
public class ItemOrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ordem_servico_id")
    @JsonIgnore
    private OrdemServico ordemServico;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Product produto; // Produto do estoque

    private Integer quantidade;
    private Double precoUnitarioPeca; // Preço da peça no momento da OS
    private Double valorTotalPeca; // quantidade * precoUnitarioPeca

    private Double valorConserto; // Valor do serviço para este produto
    private Double subtotal; // valorTotalPeca + valorConserto

    private String observacao;
}