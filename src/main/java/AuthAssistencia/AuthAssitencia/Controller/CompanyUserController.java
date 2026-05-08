package AuthAssistencia.AuthAssitencia.Controller;

import AuthAssistencia.AuthAssitencia.Model.User;
import AuthAssistencia.AuthAssitencia.Repository.UserRespository;
import AuthAssistencia.AuthAssitencia.Service.CompanyUserService;
import AuthAssistencia.AuthAssitencia.dto.CreateUserRequest;
import AuthAssistencia.AuthAssitencia.dto.PaymentInfoResponse;
import AuthAssistencia.AuthAssitencia.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/company/users")
@CrossOrigin(origins = "*")
public class CompanyUserController {

    @Autowired
    private CompanyUserService companyUserService;

    @Autowired
    private UserRespository userRepository;


    // Adicionar novo usuário (funcionário)
    @PostMapping
    public ResponseEntity<?> addUser(@Valid @RequestBody CreateUserRequest request,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User currentUser = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            UserResponse response = companyUserService.addUserToCompany(
                    currentUser.getCompany().getId(),
                    request,
                    currentUser.getId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Listar todos os usuários da empresa do logado
    @GetMapping
    public ResponseEntity<?> getCompanyUsers(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User currentUser = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            List<UserResponse> users = companyUserService.getUsersByCompany(currentUser.getCompany().getId());
            return ResponseEntity.ok(Map.of("total", users.size(), "users", users));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Remover um usuário da empresa
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> removeUser(@PathVariable Long userId,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User currentUser = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            companyUserService.removeUserFromCompany(currentUser.getCompany().getId(), userId, currentUser.getId());
            return ResponseEntity.ok(Map.of("message", "Usuário removido da empresa com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Promover um usuário a ADMIN
    @PutMapping("/{userId}/promote")
    public ResponseEntity<?> promoteUser(@PathVariable Long userId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User currentUser = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            UserResponse response = companyUserService.promoteToAdmin(
                    currentUser.getCompany().getId(),
                    userId,
                    currentUser.getId()
            );
            return ResponseEntity.ok(Map.of("message", "Usuário promovido a ADMIN", "user", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/payment-info")
    public ResponseEntity<?> getPaymentInfo() {
        try {
            PaymentInfoResponse response = companyUserService.getPaymentInfo();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Isso vai mostrar o erro REAL no console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}