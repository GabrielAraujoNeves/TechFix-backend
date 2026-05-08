package AuthAssistencia.AuthAssitencia.Service;

import AuthAssistencia.AuthAssitencia.Model.*;
import AuthAssistencia.AuthAssitencia.Repository.CompanyRepository;
import AuthAssistencia.AuthAssitencia.Repository.PaymentCardRepository;
import AuthAssistencia.AuthAssitencia.Repository.SubscriptionRepository;
import AuthAssistencia.AuthAssitencia.Repository.UserRespository;
import AuthAssistencia.AuthAssitencia.dto.CreateUserRequest;
import AuthAssistencia.AuthAssitencia.dto.PaymentInfoResponse;
import AuthAssistencia.AuthAssitencia.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyUserService {

    @Autowired
    private UserRespository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SubscriptionService subscriptionService;

    // Adicionar um novo usuário (funcionário) à empresa do ADMIN logado
    @Transactional
    public UserResponse addUserToCompany(Long companyId, CreateUserRequest request, Long currentUserId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        // Verificar se o usuário atual é ADMIN e pertence a esta empresa
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (currentUser.getRole() != Role.ADMIN || !currentUser.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("Apenas administradores da empresa podem adicionar usuários");
        }

        // Verificar se a empresa tem assinatura ativa
        Subscription subscription = subscriptionRepository.findByCompany(company)
                .orElseThrow(() -> new RuntimeException("Empresa não possui assinatura"));
        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new RuntimeException("Assinatura inativa ou pendente. Não é possível adicionar usuários.");
        }

        // Verificar limite de usuários
        int limit = subscriptionService.getUserLimitByPlan(subscription.getPlanType());
        long currentUserCount = userRepository.countByCompanyId(companyId);
        if (currentUserCount >= limit && limit != Integer.MAX_VALUE) {
            throw new RuntimeException("Limite de usuários do plano atingido (" + limit + "). Faça upgrade.");
        }

        // Verificar se email já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado no sistema");
        }

        // Criar o novo usuário (role USER por padrão)
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(Role.USER);
        newUser.setCompany(company);
        newUser = userRepository.save(newUser);

        // Adicionar à lista da empresa (opcional, pois já está no banco)
        company.getUsers().add(newUser);
        companyRepository.save(company);

        return convertToResponse(newUser);
    }

    // Listar todos os usuários da empresa
    public List<UserResponse> getUsersByCompany(Long companyId) {
        return userRepository.findByCompanyId(companyId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Remover um usuário da empresa (apenas ADMIN pode remover, e não pode remover o dono)
    @Transactional
    public void removeUserFromCompany(Long companyId, Long userIdToRemove, Long currentUserId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (currentUser.getRole() != Role.ADMIN || !currentUser.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("Apenas administradores da empresa podem remover usuários");
        }

        User userToRemove = userRepository.findById(userIdToRemove)
                .orElseThrow(() -> new RuntimeException("Usuário a ser removido não encontrado"));

        // Impedir remoção do dono da empresa
        if (userToRemove.getId().equals(company.getOwnerId())) {
            throw new RuntimeException("Não é possível remover o dono da empresa");
        }

        // Desassociar da empresa (não deletar o registro de usuário, apenas remover vínculo)
        userToRemove.setCompany(null);
        userRepository.save(userToRemove);
        company.getUsers().remove(userToRemove);
        companyRepository.save(company);
    }

    // Promover um usuário comum a ADMIN (dentro da mesma empresa)
    @Transactional
    public UserResponse promoteToAdmin(Long companyId, Long userIdToPromote, Long currentUserId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (currentUser.getRole() != Role.ADMIN || !currentUser.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("Apenas administradores da empresa podem promover outros usuários");
        }

        User userToPromote = userRepository.findById(userIdToPromote)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (!userToPromote.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("O usuário não pertence a esta empresa");
        }
        if (userToPromote.getRole() == Role.ADMIN) {
            throw new RuntimeException("Usuário já é administrador");
        }

        userToPromote.setRole(Role.ADMIN);
        userRepository.save(userToPromote);

        return convertToResponse(userToPromote);
    }

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

    private Company getCurrentUserCompany() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Company company = user.getCompany();
        if (company == null) throw new RuntimeException("Company not found");
        return company;
    }

    public PaymentInfoResponse getPaymentInfo() {
        Company company = getCurrentUserCompany();
        Subscription subscription = subscriptionRepository.findByCompany(company)
                .orElseThrow(() -> new RuntimeException("No subscription found"));

        PaymentMethod method = subscription.getPaymentMethod();
        PaymentInfoResponse response = new PaymentInfoResponse();
        response.setPaymentMethod(method);

        if (method == PaymentMethod.CREDIT_CARD || method == PaymentMethod.DEBIT_CARD) {
            PaymentCard card = paymentCardRepository.findByCompanyId(company.getId()).orElse(null);
            if (card != null) {
                PaymentInfoResponse.CardDetails details = new PaymentInfoResponse.CardDetails(
                        card.getLast4(),
                        card.getBrand(),
                        card.getExpiryMonth(),
                        card.getExpiryYear(),
                        card.getCardholderName(),
                        card.getCardToken()
                );
                response.setCardDetails(details);
            }
        }
        return response;
    }
}