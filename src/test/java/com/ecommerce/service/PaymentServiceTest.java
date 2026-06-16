package com.ecommerce.service;

import com.ecommerce.dto.PaymentCardRequestDto;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.PaymentMethod;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest
{
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void saveSimulatedCreditCard_WhenCardIsVisa_ShouldSaveUserWithVisaData() {
        User user = mock(User.class);
        PaymentCardRequestDto dto = mock(PaymentCardRequestDto.class);

        when(dto.getCardNumber()).thenReturn("4111 1111 1111 1111");
        when(dto.getCardHolderName()).thenReturn("Juan Perez");
        when(dto.getExpirationMonth()).thenReturn(12);
        when(dto.getExpirationYear()).thenReturn(2030);

        paymentService.saveSimulatedCreditCard(user, dto);

        verify(user).setPaymentMethod(PaymentMethod.CREDIT_CARD);
        verify(user).setCardHolderName("Juan Perez");
        verify(user).setCardBrand("VISA");
        verify(user).setCardLastFourDigits("1111");
        verify(user).setCardExpirationMonth(12);
        verify(user).setCardExpirationYear(2030);

        verify(userRepository).save(user);
    }

    @Test
    void saveSimulatedCreditCard_WhenCardIsMastercard_ShouldSaveUserWithMastercardBrand() {
        User user = mock(User.class);
        PaymentCardRequestDto dto = mock(PaymentCardRequestDto.class);

        when(dto.getCardNumber()).thenReturn("5555 5555 5555 4444");
        when(dto.getCardHolderName()).thenReturn("Maria Gomez");
        when(dto.getExpirationMonth()).thenReturn(10);
        when(dto.getExpirationYear()).thenReturn(2029);

        paymentService.saveSimulatedCreditCard(user, dto);

        verify(user).setPaymentMethod(PaymentMethod.CREDIT_CARD);
        verify(user).setCardHolderName("Maria Gomez");
        verify(user).setCardBrand("MASTERCARD");
        verify(user).setCardLastFourDigits("4444");
        verify(user).setCardExpirationMonth(10);
        verify(user).setCardExpirationYear(2029);

        verify(userRepository).save(user);
    }

    @Test
    void saveSimulatedCreditCard_WhenCardIsAmexStartingWith34_ShouldSaveUserWithAmexBrand() {
        User user = mock(User.class);
        PaymentCardRequestDto dto = mock(PaymentCardRequestDto.class);

        when(dto.getCardNumber()).thenReturn("3400 0000 0000 009");
        when(dto.getCardHolderName()).thenReturn("Carlos Ruiz");
        when(dto.getExpirationMonth()).thenReturn(8);
        when(dto.getExpirationYear()).thenReturn(2028);

        paymentService.saveSimulatedCreditCard(user, dto);

        verify(user).setPaymentMethod(PaymentMethod.CREDIT_CARD);
        verify(user).setCardHolderName("Carlos Ruiz");
        verify(user).setCardBrand("AMEX");
        verify(user).setCardLastFourDigits("0009");
        verify(user).setCardExpirationMonth(8);
        verify(user).setCardExpirationYear(2028);

        verify(userRepository).save(user);
    }

    @Test
    void saveSimulatedCreditCard_WhenCardIsAmexStartingWith37_ShouldSaveUserWithAmexBrand() {
        User user = mock(User.class);
        PaymentCardRequestDto dto = mock(PaymentCardRequestDto.class);

        when(dto.getCardNumber()).thenReturn("3700 0000 0000 002");
        when(dto.getCardHolderName()).thenReturn("Laura Diaz");
        when(dto.getExpirationMonth()).thenReturn(7);
        when(dto.getExpirationYear()).thenReturn(2027);

        paymentService.saveSimulatedCreditCard(user, dto);

        verify(user).setPaymentMethod(PaymentMethod.CREDIT_CARD);
        verify(user).setCardHolderName("Laura Diaz");
        verify(user).setCardBrand("AMEX");
        verify(user).setCardLastFourDigits("0002");
        verify(user).setCardExpirationMonth(7);
        verify(user).setCardExpirationYear(2027);

        verify(userRepository).save(user);
    }

    @Test
    void saveSimulatedCreditCard_WhenCardBrandIsUnknown_ShouldSaveUserWithUnknownBrand() {
        User user = mock(User.class);
        PaymentCardRequestDto dto = mock(PaymentCardRequestDto.class);

        when(dto.getCardNumber()).thenReturn("6011 0000 0000 0004");
        when(dto.getCardHolderName()).thenReturn("Ana Torres");
        when(dto.getExpirationMonth()).thenReturn(6);
        when(dto.getExpirationYear()).thenReturn(2026);

        paymentService.saveSimulatedCreditCard(user, dto);

        verify(user).setPaymentMethod(PaymentMethod.CREDIT_CARD);
        verify(user).setCardHolderName("Ana Torres");
        verify(user).setCardBrand("UNKNOWN");
        verify(user).setCardLastFourDigits("0004");
        verify(user).setCardExpirationMonth(6);
        verify(user).setCardExpirationYear(2026);

        verify(userRepository).save(user);
    }

    @Test
    void saveSimulatedCreditCard_WhenCardNumberHasSpaces_ShouldNormalizeCardNumberBeforeSaving() {
        User user = mock(User.class);
        PaymentCardRequestDto dto = mock(PaymentCardRequestDto.class);

        when(dto.getCardNumber()).thenReturn("4111    1111    1111    1234");
        when(dto.getCardHolderName()).thenReturn("Pedro Lopez");
        when(dto.getExpirationMonth()).thenReturn(5);
        when(dto.getExpirationYear()).thenReturn(2031);

        paymentService.saveSimulatedCreditCard(user, dto);

        verify(user).setCardBrand("VISA");
        verify(user).setCardLastFourDigits("1234");
        verify(userRepository).save(user);
    }

    @Test
    void saveSimulatedCreditCard_WhenCardNumberIsTooShort_ShouldThrowExceptionAndNotSaveUser() {
        User user = mock(User.class);
        PaymentCardRequestDto dto = mock(PaymentCardRequestDto.class);

        when(dto.getCardNumber()).thenReturn("411111");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.saveSimulatedCreditCard(user, dto)
        );

        assertEquals(
                "El número de tarjeta no tiene una longitud válida",
                exception.getMessage()
        );

        verify(userRepository, never()).save(any(User.class));
        verify(user, never()).setPaymentMethod(any());
        verify(user, never()).setCardHolderName(any());
        verify(user, never()).setCardBrand(any());
        verify(user, never()).setCardLastFourDigits(any());
        verify(user, never()).setCardExpirationMonth(any());
        verify(user, never()).setCardExpirationYear(any());
    }

    @Test
    void saveSimulatedCreditCard_WhenCardNumberIsTooLong_ShouldThrowExceptionAndNotSaveUser() {
        User user = mock(User.class);
        PaymentCardRequestDto dto = mock(PaymentCardRequestDto.class);

        when(dto.getCardNumber()).thenReturn("41111111111111111111");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.saveSimulatedCreditCard(user, dto)
        );

        assertEquals(
                "El número de tarjeta no tiene una longitud válida",
                exception.getMessage()
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void saveSimulatedCreditCard_WhenCardNumberContainsLetters_ShouldThrowExceptionAndNotSaveUser() {
        User user = mock(User.class);
        PaymentCardRequestDto dto = mock(PaymentCardRequestDto.class);

        when(dto.getCardNumber()).thenReturn("4111abcd11112222");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.saveSimulatedCreditCard(user, dto)
        );

        assertEquals(
                "El número de tarjeta no tiene una longitud válida",
                exception.getMessage()
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void saveSimulatedCreditCard_WhenCardNumberIsNull_ShouldThrowExceptionAndNotSaveUser() {
        User user = mock(User.class);
        PaymentCardRequestDto dto = mock(PaymentCardRequestDto.class);

        when(dto.getCardNumber()).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.saveSimulatedCreditCard(user, dto)
        );

        assertEquals(
                "El número de tarjeta no tiene una longitud válida",
                exception.getMessage()
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void saveSimulatedCreditCard_WhenCardNumberIsBlank_ShouldThrowExceptionAndNotSaveUser() {
        User user = mock(User.class);
        PaymentCardRequestDto dto = mock(PaymentCardRequestDto.class);

        when(dto.getCardNumber()).thenReturn("     ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.saveSimulatedCreditCard(user, dto)
        );

        assertEquals(
                "El número de tarjeta no tiene una longitud válida",
                exception.getMessage()
        );

        verify(userRepository, never()).save(any(User.class));
    }
}