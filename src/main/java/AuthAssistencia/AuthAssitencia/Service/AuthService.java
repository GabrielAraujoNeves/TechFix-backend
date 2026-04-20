package AuthAssistencia.AuthAssitencia.Service;

import AuthAssistencia.AuthAssitencia.Model.User;
import AuthAssistencia.AuthAssitencia.Model.Role;  // ← ADICIONE ESTA IMPORTAÇÃO
import AuthAssistencia.AuthAssitencia.Repository.UserRespository;
import AuthAssistencia.AuthAssitencia.Security.JwtUtil;
import AuthAssistencia.AuthAssitencia.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRespository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    // Registro
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getRole() != null && request.getRole().equalsIgnoreCase("ADMIN")) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }

        userRepository.save(user);

        return new RegisterResponse("User registered successfully with role: " + user.getRole(), true);
    }

    // Login - retorna token com role
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Gerar token com role
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().toString());

        return new AuthResponse(token, user.getRole().toString(), "Login successful! Role: " + user.getRole());
    }

    // Verificar token
    public VerifyResponse verifyToken(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);
            boolean isExpired = jwtUtil.isTokenExpired(token);

            if (isExpired) {
                return new VerifyResponse(false, email, role, "Token has expired");
            }

            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                return new VerifyResponse(false, email, role, "User not found");
            }

            return new VerifyResponse(true, email, role, "Token is valid. Role: " + role);

        } catch (Exception e) {
            return new VerifyResponse(false, null, null, "Invalid token: " + e.getMessage());
        }
    }

    public boolean isAdmin(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        return user != null && user.getRole() == Role.ADMIN;
    }
}