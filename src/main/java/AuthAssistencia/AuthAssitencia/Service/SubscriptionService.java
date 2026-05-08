package AuthAssistencia.AuthAssitencia.Service;

import AuthAssistencia.AuthAssitencia.Model.*;
import AuthAssistencia.AuthAssitencia.Repository.CompanyRepository;
import AuthAssistencia.AuthAssitencia.Repository.SubscriptionRepository;
import AuthAssistencia.AuthAssitencia.Repository.UserRespository;
import AuthAssistencia.AuthAssitencia.dto.PlanResponse;
import AuthAssistencia.AuthAssitencia.dto.SubscriptionResponse;
import AuthAssistencia.AuthAssitencia.dto.UpgradeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    private UserRespository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    // Retorna a empresa do usuário autenticado
    private Company getCurrentUserCompany() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Company company = user.getCompany();
        if (company == null) {
            throw new RuntimeException("User does not have a company associated");
        }
        return company;
    }

    // Lista os planos disponíveis (mock)
    public List<PlanResponse> getAvailablePlans() {
        return Arrays.asList(
                new PlanResponse("Básico", "Até 5 funcionários, até 100 ordens de serviço/mês, suporte básico", 39.90),
                new PlanResponse("Pro", "Até 15 funcionários, ordens ilimitadas, controle de estoque, relatórios avançados", 89.90),
                new PlanResponse("Premium", "Usuários ilimitados, acesso completo, suporte prioritário", 149.90)
        );
    }

    // Confirma o pagamento e ativa a assinatura
    @Transactional
    public SubscriptionResponse confirmPayment(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (subscription.getStatus() != SubscriptionStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Subscription already confirmed, active, expired or canceled.");
        }

        // Simulação de pagamento aprovado
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = calculateEndDate(now, subscription.getPaymentPeriod());

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(now);
        subscription.setEndDate(endDate);
        subscription = subscriptionRepository.save(subscription);

        String message = String.format("Pagamento confirmado. Assinatura %s ativa até %s",
                subscription.getPlanType(), subscription.getEndDate());

        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getPlanType(),
                subscription.getPaymentPeriod(),
                subscription.getStatus(),
                subscription.getPaymentMethod(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                message
        );
    }

    // Consulta a assinatura atual da empresa logada
    public SubscriptionResponse getCurrentSubscription() {
        Company company = getCurrentUserCompany();
        Subscription subscription = subscriptionRepository.findByCompany(company).orElse(null);

        if (subscription == null) {
            return new SubscriptionResponse(null, null, null, null, null, null, null,
                    "Nenhuma assinatura encontrada para esta empresa.");
        }

        // Se estiver ativa mas expirou, atualiza status
        if (subscription.getStatus() == SubscriptionStatus.ACTIVE &&
                subscription.getEndDate() != null &&
                subscription.getEndDate().isBefore(LocalDateTime.now())) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(subscription);
        }

        String msg = subscription.getStatus() == SubscriptionStatus.ACTIVE ? "Assinatura ativa" : "Assinatura " + subscription.getStatus();
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getPlanType(),
                subscription.getPaymentPeriod(),
                subscription.getStatus(),
                subscription.getPaymentMethod(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                msg
        );
    }

    // Retorna o limite de usuários por tipo de plano (usado no CompanyUserService)
    public int getUserLimitByPlan(PlanType plan) {
        return switch (plan) {
            case BASIC -> 5;
            case PRO -> 15;
            case PREMIUM -> Integer.MAX_VALUE; // ilimitado
        };
    }

    // Calcula a data de fim com base no período de pagamento
    private LocalDateTime calculateEndDate(LocalDateTime startDate, PaymentPeriod period) {
        return switch (period) {
            case MONTHLY -> startDate.plusDays(30);
            case QUARTERLY -> startDate.plusDays(90);
            case ANNUAL -> startDate.plusDays(365);
        };
    }

    @Transactional
    public SubscriptionResponse upgradePlan(UpgradeRequest request, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Company company = user.getCompany();
        if (company == null) throw new RuntimeException("Company not found");

        Subscription subscription = subscriptionRepository.findByCompany(company)
                .orElseThrow(() -> new RuntimeException("Subscription not found for this company"));

        // Atualiza a assinatura existente (não cria uma nova)
        subscription.setPlanType(request.getNewPlan());
        subscription.setPaymentPeriod(request.getPeriod());
        subscription.setPaymentMethod(request.getPaymentMethod());
        subscription.setStatus(SubscriptionStatus.PENDING_PAYMENT);
        subscription.setStartDate(null);
        subscription.setEndDate(null);

        subscription = subscriptionRepository.save(subscription);

        String message = String.format("Upgrade solicitado para %s. Aguardando confirmação de pagamento.",
                request.getNewPlan());
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getPlanType(),
                subscription.getPaymentPeriod(),
                subscription.getStatus(),
                subscription.getPaymentMethod(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                message
        );
    }
}