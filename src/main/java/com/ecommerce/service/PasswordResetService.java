package com.ecommerce.service;

import com.ecommerce.model.PasswordResetToken;
import com.ecommerce.model.User;
import com.ecommerce.repository.PasswordResetTokenRepository;
import com.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetService
{
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Optional<String> requestPasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();

        tokenRepository.deleteByUser(user);

        String token = generateToken();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(30))
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        String resetLink = "/reset-password?token=" + token;

        return Optional.of(resetLink);
    }

    @Transactional
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Este enlace ya ha sido utilizado");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El enlace ha expirado");
        }

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        resetToken.setUsed(true);

        userRepository.save(user);
        tokenRepository.save(resetToken);
    }

    @Transactional
    public boolean isValidToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(resetToken -> !resetToken.isUsed())
                .filter(resetToken -> resetToken.getExpiryDate().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
