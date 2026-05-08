package AuthAssistencia.AuthAssitencia.Repository;

import AuthAssistencia.AuthAssitencia.Model.Company;
import AuthAssistencia.AuthAssitencia.Model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByCompany(Company company);
}