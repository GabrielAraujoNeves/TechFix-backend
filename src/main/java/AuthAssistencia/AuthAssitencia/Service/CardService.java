package AuthAssistencia.AuthAssitencia.Service;

import AuthAssistencia.AuthAssitencia.Model.PaymentCard;
import AuthAssistencia.AuthAssitencia.Repository.PaymentCardRepository;
import AuthAssistencia.AuthAssitencia.dto.CardInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class CardService {

    @Autowired
    private PaymentCardRepository cardRepository;

    // Simula tokenização e salva o cartão associado a uma empresa
    public PaymentCard saveCard(Long companyId, CardInfo cardInfo) {
        // Extrai últimos 4 dígitos
        String cardNumber = cardInfo.getCardNumber().replaceAll("\\s", "");
        String last4 = cardNumber.substring(Math.max(0, cardNumber.length() - 4));

        // Detecta bandeira (simplificado)
        String brand = detectBrand(cardNumber);

        // Parse expiry date (MM/yy)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth expiry = YearMonth.parse(cardInfo.getExpiryDate().trim(), formatter);

        // Gera um token simulado (numa integração real, o gateway retornaria o token)
        String token = UUID.randomUUID().toString();

        PaymentCard card = new PaymentCard();
        card.setCompanyId(companyId);
        card.setCardToken(token);
        card.setLast4(last4);
        card.setBrand(brand);
        card.setExpiryMonth(expiry.getMonthValue());
        card.setExpiryYear(expiry.getYear());
        card.setCardholderName(cardInfo.getCardholderName());

        return cardRepository.save(card);
    }

    private String detectBrand(String cardNumber) {
        if (cardNumber.startsWith("4")) return "VISA";
        if (cardNumber.startsWith("5")) return "MASTERCARD";
        if (cardNumber.startsWith("3")) return "AMEX";
        return "OTHER";
    }
}