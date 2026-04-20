package AuthAssistencia.AuthAssitencia.Service;

import AuthAssistencia.AuthAssitencia.Model.Role;
import AuthAssistencia.AuthAssitencia.Model.User;
import AuthAssistencia.AuthAssitencia.Repository.UserRespository;
import AuthAssistencia.AuthAssitencia.dto.UserResponse;
import AuthAssistencia.AuthAssitencia.dto.UserUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAdminService {

    @Autowired
    private UserRespository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Listar todos usuários
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Buscar usuário por ID
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));
        return convertToResponse(user);
    }

    // Buscar usuário por email
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com email: " + email));
        return convertToResponse(user);
    }

    // Atualizar usuário
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // Verificar se email já existe para outro usuário
            if (userRepository.existsByEmail(request.getEmail()) &&
                    !user.getEmail().equals(request.getEmail())) {
                throw new RuntimeException("Email já está em uso por outro usuário!");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getRole() != null && !request.getRole().isEmpty()) {
            user.setRole(Role.valueOf(request.getRole()));
        }

        User updated = userRepository.save(user);
        return convertToResponse(updated);
    }

    // Promover para ADMIN
    public UserResponse promoteToAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Usuário já é ADMIN!");
        }

        user.setRole(Role.ADMIN);
        User updated = userRepository.save(user);
        return convertToResponse(updated);
    }

    // Rebaixar para USER
    public UserResponse demoteToUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        if (user.getRole() == Role.USER) {
            throw new RuntimeException("Usuário já é USER!");
        }

        // Impedir rebaixar o último ADMIN
        long adminCount = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .count();

        if (adminCount <= 1 && user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Não é possível rebaixar o único ADMIN do sistema!");
        }

        user.setRole(Role.USER);
        User updated = userRepository.save(user);
        return convertToResponse(updated);
    }

    // Deletar usuário
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        // Impedir deletar o último ADMIN
        long adminCount = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .count();

        if (adminCount <= 1 && user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Não é possível deletar o único ADMIN do sistema!");
        }

        userRepository.delete(user);
    }

    // Resetar senha
    public void resetPassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("Nova senha deve ter pelo menos 6 caracteres!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Converter User para UserResponse
    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().toString(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}