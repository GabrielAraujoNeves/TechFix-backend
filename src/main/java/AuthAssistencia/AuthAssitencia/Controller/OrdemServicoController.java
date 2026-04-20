package AuthAssistencia.AuthAssitencia.Controller;

import AuthAssistencia.AuthAssitencia.Service.OrdemServicoService;
import AuthAssistencia.AuthAssitencia.dto.AtualizarStatusRequest;
import AuthAssistencia.AuthAssitencia.dto.OrdemServicoRequest;
import AuthAssistencia.AuthAssitencia.dto.OrdemServicoResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/os")
@CrossOrigin(origins = "*")
public class OrdemServicoController {

    @Autowired
    private OrdemServicoService ordemServicoService;

    // ========== ENDPOINTS ACESSÍVEIS POR USER E ADMIN ==========

    // Criar Ordem de Serviço (USER e ADMIN)
    @PostMapping("/create")
    public ResponseEntity<?> createOrdemServico(@Valid @RequestBody OrdemServicoRequest request) {
        try {
            OrdemServicoResponse response = ordemServicoService.createOrdemServico(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro interno: " + e.getMessage()));
        }
    }

    // Listar todas as OS (USER e ADMIN)
    @GetMapping("/all")
    public ResponseEntity<?> getAllOrdens() {
        try {
            List<OrdemServicoResponse> ordens = ordemServicoService.getAllOrdens();
            return ResponseEntity.ok(Map.of(
                    "total", ordens.size(),
                    "ordens", ordens
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Buscar OS por ID (USER e ADMIN)
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrdemById(@PathVariable Long id) {
        try {
            OrdemServicoResponse ordem = ordemServicoService.getOrdemById(id);
            return ResponseEntity.ok(ordem);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Buscar OS por número (USER e ADMIN)
    @GetMapping("/numero/{numeroOS}")
    public ResponseEntity<?> getOrdemByNumero(@PathVariable String numeroOS) {
        try {
            OrdemServicoResponse ordem = ordemServicoService.getOrdemByNumero(numeroOS);
            return ResponseEntity.ok(ordem);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Buscar OS por status (USER e ADMIN)
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdensByStatus(@PathVariable String status) {
        try {
            List<OrdemServicoResponse> ordens = ordemServicoService.getOrdensByStatus(status);
            return ResponseEntity.ok(Map.of(
                    "total", ordens.size(),
                    "status", status,
                    "ordens", ordens
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Status inválido. Use: PENDENTE, CONSERTANDO, FINALIZADO, CANCELADO"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Buscar OS por cliente (USER e ADMIN)
    @GetMapping("/cliente/{nome}")
    public ResponseEntity<?> getOrdensByCliente(@PathVariable String nome) {
        try {
            List<OrdemServicoResponse> ordens = ordemServicoService.getOrdensByCliente(nome);
            return ResponseEntity.ok(Map.of(
                    "total", ordens.size(),
                    "cliente", nome,
                    "ordens", ordens
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Atualizar status da OS (USER e ADMIN)
    @PutMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @Valid @RequestBody AtualizarStatusRequest request) {
        try {
            OrdemServicoResponse response = ordemServicoService.updateStatus(id, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Status atualizado com sucesso!",
                    "ordem", response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ========== ENDPOINTS EXCLUSIVOS PARA ADMIN ==========

    // Cancelar OS (apenas ADMIN)
    @PutMapping("/admin/cancel/{id}")
    public ResponseEntity<?> cancelOrdem(@PathVariable Long id,
                                         @RequestParam String motivo) {
        try {
            OrdemServicoResponse response = ordemServicoService.cancelOrdem(id, motivo);
            return ResponseEntity.ok(Map.of(
                    "message", "OS cancelada com sucesso!",
                    "ordem", response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Deletar OS (apenas ADMIN)
    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<?> deleteOrdem(@PathVariable Long id) {
        try {
            ordemServicoService.deleteOrdem(id);
            return ResponseEntity.ok(Map.of("message", "Ordem de Serviço deletada com sucesso!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}