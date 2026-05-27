package com.ecommerce.controller;

import com.ecommerce.dto.ChatbotRequestDto;
import com.ecommerce.dto.ChatbotResponseDto;
import com.ecommerce.service.ChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    // Página completa del asistente, útil para enseñar la funcionalidad en la presentación.
    @GetMapping("/chatbot")
    public String chatbotPage() {
        return "chatbot/chatbot";
    }

    // Lista de conversaciones guardadas del usuario que ha iniciado sesión.
    @GetMapping("/chatbot/history")
    public String chatbotHistory(Model model, Authentication authentication) {
        model.addAttribute("conversations", chatbotService.findHistoryForUser(authentication.getName()));
        return "chatbot/history";
    }

    // Detalle de una conversación concreta, validando en el servicio que sea del usuario.
    @GetMapping("/chatbot/history/{id}")
    public String chatbotHistoryDetail(@PathVariable UUID id, Model model, Authentication authentication) {
        model.addAttribute("conversation", chatbotService.findConversationForUser(id, authentication.getName()));
        return "chatbot/history-detail";
    }

    // Endpoint AJAX usado por la página completa y por el widget flotante.
    @PostMapping("/api/chatbot")
    @ResponseBody
    public ChatbotResponseDto ask(@Valid @RequestBody ChatbotRequestDto request, Authentication authentication) {
        // Si no hay sesión, el chat funciona igualmente, pero la conversación queda sin usuario asociado.
        String username = authentication != null && authentication.isAuthenticated() ? authentication.getName() : null;
        return chatbotService.answer(request.message(), request.conversationId(), username);
    }
}
