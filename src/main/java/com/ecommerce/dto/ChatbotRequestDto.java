package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

// Datos que envía el navegador al backend cuando el usuario pregunta al chatbot.
public record ChatbotRequestDto(
        @NotBlank(message = "El mensaje no puede estar vacío")
        @Size(max = 800, message = "El mensaje no puede superar los 800 caracteres")
        String message,

        // Permite continuar el mismo hilo en vez de crear uno nuevo en cada mensaje.
        UUID conversationId
) {
}
