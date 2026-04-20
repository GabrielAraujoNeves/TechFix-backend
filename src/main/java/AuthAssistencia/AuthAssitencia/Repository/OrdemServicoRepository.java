package AuthAssistencia.AuthAssitencia.Repository;

import AuthAssistencia.AuthAssitencia.Model.OrdemServico;
import AuthAssistencia.AuthAssitencia.Model.StatusOrdem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    List<OrdemServico> findByStatus(StatusOrdem status);

    List<OrdemServico> findByClienteNomeContainingIgnoreCase(String nome);

    List<OrdemServico> findByMarcaContainingIgnoreCase(String marca);

    List<OrdemServico> findByModeloContainingIgnoreCase(String modelo);

    List<OrdemServico> findByDataAberturaBetween(LocalDateTime inicio, LocalDateTime fim);

    boolean existsByNumeroOS(String numeroOS);

    OrdemServico findByNumeroOS(String numeroOS);
}