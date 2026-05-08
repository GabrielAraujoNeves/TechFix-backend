package AuthAssistencia.AuthAssitencia.Service;

import AuthAssistencia.AuthAssitencia.Model.*;
import AuthAssistencia.AuthAssitencia.Repository.CompanyRepository;
import AuthAssistencia.AuthAssitencia.Repository.SubscriptionRepository;
import AuthAssistencia.AuthAssitencia.Repository.UserRespository;
import AuthAssistencia.AuthAssitencia.Security.JwtUtil;
import AuthAssistencia.AuthAssitencia.dto.*;
import jakarta.transaction.Transactional;
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
    private CompanyRepository companyRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // Validações iniciais
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        if (companyRepository.existsByCnpj(request.getCnpj())) {
            throw new RuntimeException("CNPJ already registered!");
        }

        // 1. Criar usuário (dono)
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);
        user = userRepository.save(user); // salva temporariamente (company será setada depois)

        // 2. Criar empresa
        Company company = new Company();
        company.setNomeEmpresa(request.getNomeEmpresa());
        company.setCnpj(request.getCnpj());
        company.setEndereco(request.getEndereco());
        company.setCidade(request.getCidade());
        company.setEstado(request.getEstado());
        company.setTelefoneComercial(request.getTelefoneComercial());
        company.setSegmentoAtuacao(request.getSegmentoAtuacao());
        company.setOwnerId(user.getId());

        // --- NOVOS CAMPOS ---
        company.setSegmentoCode(request.getSegmentoCode().toUpperCase()); // ex: "CEL"

        // Gerar edificacaoId automaticamente (ex: CEL-001, AC-005)
        String prefix = company.getSegmentoCode();
        long count = companyRepository.countBySegmentoCode(prefix);
        String edificacaoId = prefix + "-" + String.format("%03d", count + 1);
        company.setEdificacaoId(edificacaoId);

        // Salvar a empresa (agora com os campos obrigatórios preenchidos)
        company = companyRepository.save(company);

        // 3. Associar usuário à empresa
        user.setCompany(company);
        user = userRepository.save(user);

        // 4. Adicionar usuário na lista da empresa (opcional, mas bom para consistência)
        company.getUsers().add(user);
        companyRepository.save(company);

        // 5. Criar assinatura pendente
        Subscription subscription = new Subscription();
        subscription.setCompany(company);
        subscription.setPlanType(request.getPlanType());
        subscription.setPaymentPeriod(request.getPaymentPeriod());
        subscription.setPaymentMethod(request.getPaymentMethod());
        subscription.setStatus(SubscriptionStatus.PENDING_PAYMENT);
        subscription = subscriptionRepository.save(subscription);

        company.setSubscription(subscription);
        companyRepository.save(company);

        return new RegisterResponse(
                "User and company registered. Subscription created, pending payment.",
                true,
                user.getId(),
                company.getId(),
                subscription.getId()
        );
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().toString());
        return new AuthResponse(token, user.getRole().toString(), "Login successful! Role: " + user.getRole());
    }

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