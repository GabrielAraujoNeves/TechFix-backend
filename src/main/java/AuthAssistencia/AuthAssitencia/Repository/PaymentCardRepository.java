package AuthAssistencia.AuthAssitencia.Repository;

import AuthAssistencia.AuthAssitencia.Model.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {
    Optional<PaymentCard> findByCompanyId(Long companyId);
}
