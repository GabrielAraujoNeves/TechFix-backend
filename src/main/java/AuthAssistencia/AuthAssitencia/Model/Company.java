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
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_empresa", nullable = false)
    private String nomeEmpresa;

    @Column(unique = true, nullable = false)
    private String cnpj;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String estado;

    @Column(name = "telefone_comercial", nullable = false)
    private String telefoneComercial;

    @Column(name = "segmento_atuacao", nullable = false)
    private String segmentoAtuacao;

    @Column(name = "owner_id")
    private Long ownerId; // ID do usuário dono da empresa (ADMIN principal)

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "segmento_code", nullable = false, length = 20)
    private String segmentoCode; // CEL, AC, REFRIG, TI, etc.

    @Column(name = "edificacao_id")
    private String edificacaoId;

    // Relacionamento com Subscription (um para um)
    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Subscription subscription;

    // Relacionamento com User (uma empresa tem muitos usuários)
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}