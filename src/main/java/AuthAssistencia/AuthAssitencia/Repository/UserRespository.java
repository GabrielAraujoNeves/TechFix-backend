package AuthAssistencia.AuthAssitencia.Repository;

import AuthAssistencia.AuthAssitencia.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRespository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // Novos métodos para gerenciamento por empresa
    List<User> findByCompanyId(Long companyId);
    long countByCompanyId(Long companyId);
}