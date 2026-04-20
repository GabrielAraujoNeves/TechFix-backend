package AuthAssistencia.AuthAssitencia.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"nome", "modelo", "marca"}) // Evita produtos duplicados
        })
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // ID único automático

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private String marca;

    @Column(name = "preco_atacado", nullable = false)
    private Double precoAtacado;

    @Column(name = "preco_conserto_peca", nullable = false)
    private Double precoConsertoPeca;

    @Column(name = "quantidade_estoque", nullable = false)
    private Integer quantidadeEstoque;  // NOVO: controle de estoque

    @Column(name = "estoque_minimo")
    private Integer estoqueMinimo;  // NOVO: alerta de estoque baixo

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (quantidadeEstoque == null) {
            quantidadeEstoque = 0;  // Estoque inicial zero
        }
        if (estoqueMinimo == null) {
            estoqueMinimo = 5;  // Alerta quando estoque < 5
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}