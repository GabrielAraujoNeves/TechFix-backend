package AuthAssistencia.AuthAssitencia.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ordens_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String numeroOS; // Número único da OS

    @Column(nullable = false)
    private String clienteNome;

    private String clienteTelefone;
    private String clienteEmail;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private String marca;

    @Column(length = 500)
    private String problema;

    private String observacoes;

    @Enumerated(EnumType.STRING)
    private StatusOrdem status;

    private Double valorTotalConserto; // Valor total do serviço

    private Double valorTotalPecas; // Soma dos preços das peças

    private Double valorTotalGeral; // Total geral (conserto + peças)

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ItemOrdemServico> itens = new ArrayList<>();

    private LocalDateTime dataAbertura;
    private LocalDateTime dataFinalizacao;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        dataAbertura = LocalDateTime.now();
        if (status == null) {
            status = StatusOrdem.PENDENTE;
        }
        if (valorTotalConserto == null) valorTotalConserto = 0.0;
        if (valorTotalPecas == null) valorTotalPecas = 0.0;
        if (valorTotalGeral == null) valorTotalGeral = 0.0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (status == StatusOrdem.FINALIZADO && dataFinalizacao == null) {
            dataFinalizacao = LocalDateTime.now();
        }
    }
}