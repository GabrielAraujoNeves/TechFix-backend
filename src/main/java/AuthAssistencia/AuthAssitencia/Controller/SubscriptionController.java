package AuthAssistencia.AuthAssitencia.Controller;

import AuthAssistencia.AuthAssitencia.Model.Company;
import AuthAssistencia.AuthAssitencia.Repository.CompanyRepository;
import AuthAssistencia.AuthAssitencia.Service.SubscriptionService;
import AuthAssistencia.AuthAssitencia.dto.SubscriptionResponse;
import AuthAssistencia.AuthAssitencia.dto.UpgradeRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscription")
@CrossOrigin(origins = "*")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private CompanyRepository companyRepository;  // INJETE AQUI

    @GetMapping("/plans")
    public ResponseEntity<?> getPlans() {
        return ResponseEntity.ok(subscriptionService.getAvailablePlans());
    }

    @PutMapping("/{subscriptionId}/confirm-payment")
    public ResponseEntity<?> confirmPayment(@PathVariable Long subscriptionId) {
        try {
            SubscriptionResponse response = subscriptionService.confirmPayment(subscriptionId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentSubscription() {
        try {
            SubscriptionResponse response = subscriptionService.getCurrentSubscription();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Agora usando a instância injetada (e não estático)
    @GetMapping("/by-segmento/{segmentoCode}")
    public ResponseEntity<?> getCompaniesBySegmento(@PathVariable String segmentoCode) {
        List<Company> companies = companyRepository.findBySegmentoCode(segmentoCode);
        return ResponseEntity.ok(companies);
    }


    @PostMapping("/upgrade")
    public ResponseEntity<?> upgradeSubscription(@Valid @RequestBody UpgradeRequest request,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        try {
            SubscriptionResponse response = subscriptionService.upgradePlan(request, userDetails);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}