package AuthAssistencia.AuthAssitencia.Controller;

import AuthAssistencia.AuthAssitencia.Model.User;
import AuthAssistencia.AuthAssitencia.Service.UserAdminService;
import AuthAssistencia.AuthAssitencia.dto.UserResponse;
import AuthAssistencia.AuthAssitencia.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*")
public class UserAdminController {

    @Autowired
    private UserAdminService userAdminService;

    // Listar todos usuários
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserResponse> users = userAdminService.getAllUsers();
            return ResponseEntity.ok(Map.of(
                    "total", users.size(),
                    "users", users
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Buscar usuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            UserResponse user = userAdminService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Buscar usuário por email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            UserResponse user = userAdminService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Atualizar usuário
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        try {
            UserResponse updated = userAdminService.updateUser(id, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Usuário atualizado com sucesso!",
                    "user", updated
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Promover USER para ADMIN
    @PutMapping("/promote/{id}")
    public ResponseEntity<?> promoteToAdmin(@PathVariable Long id) {
        try {
            UserResponse user = userAdminService.promoteToAdmin(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Usuário promovido para ADMIN com sucesso!",
                    "user", user
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Rebaixar ADMIN para USER
    @PutMapping("/demote/{id}")
    public ResponseEntity<?> demoteToUser(@PathVariable Long id) {
        try {
            UserResponse user = userAdminService.demoteToUser(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Usuário rebaixado para USER com sucesso!",
                    "user", user
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Deletar usuário
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userAdminService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "Usuário deletado com sucesso!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Resetar senha do usuário
    @PutMapping("/reset-password/{id}")
    public ResponseEntity<?> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        try {
            userAdminService.resetPassword(id, newPassword);
            return ResponseEntity.ok(Map.of("message", "Senha resetada com sucesso!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}