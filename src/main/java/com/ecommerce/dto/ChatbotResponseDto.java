package com.ecommerce.dto;

import java.util.UUID;

// Respuesta JSON que consume el JavaScript del widget y de la página /chatbot.
public record ChatbotResponseDto(
        String answer,

        // true cuando se usa modo demo o se devuelve un error controlado.
        boolean demoMode,

        // El navegador lo guarda en localStorage para continuar la conversación.
        UUID conversationId
) {
}
