package com.ecommerce.repository;

import com.ecommerce.model.PasswordResetToken;
import com.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID>
{
    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);
}
