package com.ecommerce.service;

import com.ecommerce.dto.PaymentCardRequestDto;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.PaymentMethod;
import com.ecommerce.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PaymentService
{
    private final UserRepository userRepository;

    @Transactional
    public void saveSimulatedCreditCard(User user, PaymentCardRequestDto dto) {
        String normalizedCardNumber = normalizeCardNumber(dto.getCardNumber());

        if (!isValidCardNumberLength(normalizedCardNumber)) {
            throw new IllegalArgumentException("El número de tarjeta no tiene una longitud válida");
        }

        user.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        user.setCardHolderName(dto.getCardHolderName());
        user.setCardBrand(detectCardBrand(normalizedCardNumber));
        user.setCardLastFourDigits(extractLastFourDigits(normalizedCardNumber));
        user.setCardExpirationMonth(dto.getExpirationMonth());
        user.setCardExpirationYear(dto.getExpirationYear());

        userRepository.save(user);
    }

    private String normalizeCardNumber(String cardNumber) {
        return cardNumber == null ? "" : cardNumber.replaceAll("\\s+", "");
    }

    private boolean isValidCardNumberLength(String cardNumber) {
        return cardNumber.matches("^[0-9]{13,19}$");
    }

    private String extractLastFourDigits(String cardNumber) {
        return cardNumber.substring(cardNumber.length() - 4);
    }

    private String detectCardBrand(String cardNumber) {
        if (cardNumber.startsWith("4")) {
            return "VISA";
        }

        if (cardNumber.matches("^5[1-5].*")) {
            return "MASTERCARD";
        }

        if (cardNumber.startsWith("34") || cardNumber.startsWith("37")) {
            return "AMEX";
        }

        return "UNKNOWN";
    }
}
