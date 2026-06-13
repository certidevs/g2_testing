package com.ecommerce.controller.web;

import com.ecommerce.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PasswordResetController
{
    private final PasswordResetService passwordResetService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        Optional<String> resetLink = passwordResetService.requestPasswordReset(email);

        model.addAttribute(
                "message",
                "Si existe una cuenta asociada a ese correo, se ha generado un enlace de recuperación."
        );

        resetLink.ifPresent(link -> model.addAttribute("resetLink", link));

        return "auth/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        if (!passwordResetService.isValidToken(token)) {
            model.addAttribute("error", "El enlace no es válido o ha expirado.");
            return "auth/reset-password";
        }

        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam String token,
            @RequestParam String password,
            @RequestParam String passwordConfirm,
            Model model
    ) {
        try {
            passwordResetService.resetPassword(token, password, passwordConfirm);

            model.addAttribute(
                    "message",
                    "Contraseña actualizada correctamente. Ya puedes iniciar sesión."
            );

            return "auth/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);

            return "auth/reset-password";
        }
    }
}
