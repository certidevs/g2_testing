package com.ecommerce.controller.web;

import com.ecommerce.service.PasswordResetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class PasswordResetControllerTest
{
    private PasswordResetService passwordResetService;
    private PasswordResetController passwordResetController;

    @BeforeEach
    void setUp() {
        passwordResetService = mock(PasswordResetService.class);
        passwordResetController = new PasswordResetController(passwordResetService);
    }

    @Test
    void showForgotPasswordForm_ShouldReturnForgotPasswordView() {
        String viewName = passwordResetController.showForgotPasswordForm();

        assertEquals("auth/forgot-password", viewName);
    }

    @Test
    void processForgotPassword_WhenEmailExists_ShouldAddMessageAndResetLink() {
        String email = "test@example.com";
        String resetLink = "/reset-password?token=abc123";

        Model model = new ExtendedModelMap();

        when(passwordResetService.requestPasswordReset(email))
                .thenReturn(Optional.of(resetLink));

        String viewName = passwordResetController.processForgotPassword(email, model);

        assertEquals("auth/forgot-password", viewName);

        assertEquals(
                "Si existe una cuenta asociada a ese correo, se ha generado un enlace de recuperación.",
                model.getAttribute("message")
        );

        assertEquals(resetLink, model.getAttribute("resetLink"));

        verify(passwordResetService).requestPasswordReset(email);
    }

    @Test
    void processForgotPassword_WhenEmailDoesNotExist_ShouldAddMessageWithoutResetLink() {
        String email = "notfound@example.com";

        Model model = new ExtendedModelMap();

        when(passwordResetService.requestPasswordReset(email))
                .thenReturn(Optional.empty());

        String viewName = passwordResetController.processForgotPassword(email, model);

        assertEquals("auth/forgot-password", viewName);

        assertEquals(
                "Si existe una cuenta asociada a ese correo, se ha generado un enlace de recuperación.",
                model.getAttribute("message")
        );

        assertNull(model.getAttribute("resetLink"));

        verify(passwordResetService).requestPasswordReset(email);
    }

    @Test
    void showResetPasswordForm_WhenTokenIsValid_ShouldReturnResetPasswordViewWithToken() {
        String token = "valid-token";

        Model model = new ExtendedModelMap();

        when(passwordResetService.isValidToken(token)).thenReturn(true);

        String viewName = passwordResetController.showResetPasswordForm(token, model);

        assertEquals("auth/reset-password", viewName);
        assertEquals(token, model.getAttribute("token"));
        assertNull(model.getAttribute("error"));

        verify(passwordResetService).isValidToken(token);
    }

    @Test
    void showResetPasswordForm_WhenTokenIsInvalid_ShouldReturnResetPasswordViewWithError() {
        String token = "invalid-token";

        Model model = new ExtendedModelMap();

        when(passwordResetService.isValidToken(token)).thenReturn(false);

        String viewName = passwordResetController.showResetPasswordForm(token, model);

        assertEquals("auth/reset-password", viewName);

        assertEquals(
                "El enlace no es válido o ha expirado.",
                model.getAttribute("error")
        );

        assertNull(model.getAttribute("token"));

        verify(passwordResetService).isValidToken(token);
    }

    @Test
    void processResetPassword_WhenDataIsValid_ShouldReturnLoginViewWithSuccessMessage() {
        String token = "valid-token";
        String password = "NewPassword123!";
        String passwordConfirm = "NewPassword123!";

        Model model = new ExtendedModelMap();

        doNothing().when(passwordResetService)
                .resetPassword(token, password, passwordConfirm);

        String viewName = passwordResetController.processResetPassword(
                token,
                password,
                passwordConfirm,
                model
        );

        assertEquals("auth/login", viewName);

        assertEquals(
                "Contraseña actualizada correctamente. Ya puedes iniciar sesión.",
                model.getAttribute("message")
        );

        assertNull(model.getAttribute("error"));

        verify(passwordResetService).resetPassword(token, password, passwordConfirm);
    }

    @Test
    void processResetPassword_WhenServiceThrowsException_ShouldReturnResetPasswordViewWithError() {
        String token = "expired-token";
        String password = "NewPassword123!";
        String passwordConfirm = "NewPassword123!";
        String errorMessage = "El enlace ha expirado";

        Model model = new ExtendedModelMap();

        doThrow(new IllegalArgumentException(errorMessage))
                .when(passwordResetService)
                .resetPassword(token, password, passwordConfirm);

        String viewName = passwordResetController.processResetPassword(
                token,
                password,
                passwordConfirm,
                model
        );

        assertEquals("auth/reset-password", viewName);
        assertEquals(errorMessage, model.getAttribute("error"));
        assertEquals(token, model.getAttribute("token"));

        verify(passwordResetService).resetPassword(token, password, passwordConfirm);
    }

    @Test
    void processResetPassword_WhenPasswordsDoNotMatch_ShouldReturnResetPasswordViewWithError() {
        String token = "valid-token";
        String password = "NewPassword123!";
        String passwordConfirm = "DifferentPassword123!";
        String errorMessage = "Las contraseñas no coinciden";

        Model model = new ExtendedModelMap();

        doThrow(new IllegalArgumentException(errorMessage))
                .when(passwordResetService)
                .resetPassword(token, password, passwordConfirm);

        String viewName = passwordResetController.processResetPassword(
                token,
                password,
                passwordConfirm,
                model
        );

        assertEquals("auth/reset-password", viewName);
        assertEquals(errorMessage, model.getAttribute("error"));
        assertEquals(token, model.getAttribute("token"));

        verify(passwordResetService).resetPassword(token, password, passwordConfirm);
    }
}