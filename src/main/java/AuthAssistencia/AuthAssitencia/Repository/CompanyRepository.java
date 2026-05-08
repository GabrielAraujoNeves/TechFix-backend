package AuthAssistencia.AuthAssitencia.Repository;

import AuthAssistencia.AuthAssitencia.Model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByCnpj(String cnpj);
    boolean existsByCnpj(String cnpj);
    List<Company> findBySegmentoCode(String segmentoCode);

    // NOVO: conta quantas empresas existem com um determinado segmentoCode
    long countBySegmentoCode(String segmentoCode);
}