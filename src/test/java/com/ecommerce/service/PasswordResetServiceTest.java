package com.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import com.ecommerce.model.PasswordResetToken;
import com.ecommerce.repository.PasswordResetTokenRepository;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

// Cambia estos imports por los packages reales de tu proyecto
import com.ecommerce.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private static final String EMAIL = "test@example.com";
    private static final String TOKEN = "valid-token";
    private static final String NEW_PASSWORD = "NewPassword123!";
    private static final String ENCODED_PASSWORD = "encoded-password";

    @Test
    void requestPasswordReset_WhenUserExists_ShouldDeletePreviousTokensSaveNewTokenAndReturnResetLink()
    {
        User user = mock(User.class);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        LocalDateTime beforeExecution = LocalDateTime.now();

        Optional<String> result = passwordResetService.requestPasswordReset(EMAIL);

        LocalDateTime afterExecution = LocalDateTime.now();

        assertThat(result).isPresent();
        assertThat(result.get()).startsWith("/reset-password?token=");

        verify(userRepository).findByEmail(EMAIL);
        verify(tokenRepository).deleteByUser(user);

        ArgumentCaptor<PasswordResetToken> tokenCaptor =
                ArgumentCaptor.forClass(PasswordResetToken.class);

        verify(tokenRepository).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();

        assertThat(savedToken.getToken()).isNotBlank();
        assertThat(savedToken.getUser()).isSameAs(user);
        assertThat(savedToken.isUsed()).isFalse();

        assertThat(savedToken.getExpiryDate())
                .isAfterOrEqualTo(beforeExecution.plusMinutes(30))
                .isBeforeOrEqualTo(afterExecution.plusMinutes(30));

        String tokenFromLink = result.get().replace("/reset-password?token=", "");
        assertThat(tokenFromLink).isEqualTo(savedToken.getToken());
    }

    @Test
    void requestPasswordReset_WhenUserDoesNotExist_ShouldReturnEmptyAndNotCreateToken() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        Optional<String> result = passwordResetService.requestPasswordReset(EMAIL);

        assertThat(result).isEmpty();

        verify(userRepository).findByEmail(EMAIL);
        verifyNoInteractions(tokenRepository);
    }

    @Test
    void resetPassword_WhenTokenIsValid_ShouldEncodePasswordMarkTokenAsUsedAndSaveChanges() {
        User user = mock(User.class);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(TOKEN)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();

        when(tokenRepository.findByToken(TOKEN)).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        passwordResetService.resetPassword(TOKEN, NEW_PASSWORD, NEW_PASSWORD);

        verify(tokenRepository).findByToken(TOKEN);
        verify(passwordEncoder).encode(NEW_PASSWORD);
        verify(user).setPassword(ENCODED_PASSWORD);
        verify(userRepository).save(user);
        verify(tokenRepository).save(resetToken);

        assertThat(resetToken.isUsed()).isTrue();
    }

    @Test
    void resetPassword_WhenNewPasswordIsNull_ShouldThrowException() {
        assertThatThrownBy(() ->
                passwordResetService.resetPassword(TOKEN, null, null)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La contraseña no puede estar vacía");

        verifyNoInteractions(tokenRepository);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userRepository);
    }

    @Test
    void resetPassword_WhenNewPasswordIsBlank_ShouldThrowException() {
        assertThatThrownBy(() ->
                passwordResetService.resetPassword(TOKEN, "   ", "   ")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La contraseña no puede estar vacía");

        verifyNoInteractions(tokenRepository);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userRepository);
    }

    @Test
    void resetPassword_WhenPasswordsDoNotMatch_ShouldThrowException() {
        assertThatThrownBy(() ->
                passwordResetService.resetPassword(TOKEN, NEW_PASSWORD, "DifferentPassword123!")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Las contraseñas no coinciden");

        verifyNoInteractions(tokenRepository);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userRepository);
    }

    @Test
    void resetPassword_WhenTokenDoesNotExist_ShouldThrowException() {
        when(tokenRepository.findByToken(TOKEN)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                passwordResetService.resetPassword(TOKEN, NEW_PASSWORD, NEW_PASSWORD)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token inválido");

        verify(tokenRepository).findByToken(TOKEN);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userRepository);
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void resetPassword_WhenTokenIsAlreadyUsed_ShouldThrowException() {
        User user = mock(User.class);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(TOKEN)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .used(true)
                .build();

        when(tokenRepository.findByToken(TOKEN)).thenReturn(Optional.of(resetToken));

        assertThatThrownBy(() ->
                passwordResetService.resetPassword(TOKEN, NEW_PASSWORD, NEW_PASSWORD)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Este enlace ya ha sido utilizado");

        verify(tokenRepository).findByToken(TOKEN);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userRepository);
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void resetPassword_WhenTokenIsExpired_ShouldThrowException() {
        User user = mock(User.class);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(TOKEN)
                .user(user)
                .expiryDate(LocalDateTime.now().minusMinutes(1))
                .used(false)
                .build();

        when(tokenRepository.findByToken(TOKEN)).thenReturn(Optional.of(resetToken));

        assertThatThrownBy(() ->
                passwordResetService.resetPassword(TOKEN, NEW_PASSWORD, NEW_PASSWORD)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El enlace ha expirado");

        verify(tokenRepository).findByToken(TOKEN);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userRepository);
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void isValidToken_WhenTokenExistsIsNotUsedAndNotExpired_ShouldReturnTrue() {
        User user = mock(User.class);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(TOKEN)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();

        when(tokenRepository.findByToken(TOKEN)).thenReturn(Optional.of(resetToken));

        boolean result = passwordResetService.isValidToken(TOKEN);

        assertThat(result).isTrue();

        verify(tokenRepository).findByToken(TOKEN);
    }

    @Test
    void isValidToken_WhenTokenDoesNotExist_ShouldReturnFalse() {
        when(tokenRepository.findByToken(TOKEN)).thenReturn(Optional.empty());

        boolean result = passwordResetService.isValidToken(TOKEN);

        assertThat(result).isFalse();

        verify(tokenRepository).findByToken(TOKEN);
    }

    @Test
    void isValidToken_WhenTokenIsUsed_ShouldReturnFalse() {
        User user = mock(User.class);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(TOKEN)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .used(true)
                .build();

        when(tokenRepository.findByToken(TOKEN)).thenReturn(Optional.of(resetToken));

        boolean result = passwordResetService.isValidToken(TOKEN);

        assertThat(result).isFalse();

        verify(tokenRepository).findByToken(TOKEN);
    }

    @Test
    void isValidToken_WhenTokenIsExpired_ShouldReturnFalse() {
        User user = mock(User.class);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(TOKEN)
                .user(user)
                .expiryDate(LocalDateTime.now().minusMinutes(1))
                .used(false)
                .build();

        when(tokenRepository.findByToken(TOKEN)).thenReturn(Optional.of(resetToken));

        boolean result = passwordResetService.isValidToken(TOKEN);

        assertThat(result).isFalse();

        verify(tokenRepository).findByToken(TOKEN);
    }
}