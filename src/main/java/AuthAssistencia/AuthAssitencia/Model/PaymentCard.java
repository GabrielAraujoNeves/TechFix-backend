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
@Table(name = "payment_cards")
public class PaymentCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "card_token", nullable = false, unique = true)
    private String cardToken;   // token simulado (UUID)

    @Column(name = "last4", nullable = false)
    private String last4;       // últimos 4 dígitos

    @Column(name = "brand", nullable = false)
    private String brand;       // VISA, MASTERCARD, ELO, etc.

    @Column(name = "expiry_month", nullable = false)
    private Integer expiryMonth; // 1-12

    @Column(name = "expiry_year", nullable = false)
    private Integer expiryYear;  // ano com 4 dígitos

    @Column(name = "cardholder_name", nullable = false)
    private String cardholderName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}