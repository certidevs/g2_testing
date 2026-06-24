package com.ecommerce.service;

import com.ecommerce.dto.ChatbotResponseDto;
import com.ecommerce.model.Conversation;
import com.ecommerce.model.Message;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.ConversationRepository;
import com.ecommerce.repository.MessageRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private static final int PRODUCT_CONTEXT_LIMIT = 20;

    private final ProductRepository productRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;

    // La clave nunca se guarda en Git: Spring la lee desde la variable de entorno GEMINI_API_KEY.
    @Value("${gemini.api-key:${GEMINI_API_KEY:}}")
    private String apiKey;

    // El modelo se puede cambiar con GEMINI_MODEL sin tocar el código.
    @Value("${gemini.model:gemini-2.5-flash}")
    private String model;

    // Historial visible para el usuario autenticado, ordenado por la conversación más reciente.
    @Transactional(readOnly = true)
    public List<Conversation> findHistoryForUser(String username) {
        User user = findUser(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return conversationRepository.findByUserOrderByUpdatedAtDesc(user);
    }

    // Protege el detalle del historial: un usuario solo puede abrir sus propias conversaciones.
    @Transactional(readOnly = true)
    public Conversation findConversationForUser(UUID conversationId, String username) {
        User user = findUser(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return conversationRepository.findByIdAndUser(conversationId, user)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));
    }

    // Flujo principal: guarda la pregunta, llama a Gemini y guarda la respuesta.
    @Transactional
    public ChatbotResponseDto answer(String message, UUID conversationId, String username) {
        String cleanMessage = sanitize(message);
        User user = findUser(username).orElse(null);
        Conversation conversation = resolveConversation(conversationId, user, cleanMessage);

        saveMessage(conversation, cleanMessage, "USER");

        // Si no hay clave, el proyecto sigue funcionando para demo y también guarda el intercambio.
        if (!StringUtils.hasText(apiKey)) {
            String demoAnswer = buildDemoAnswer(cleanMessage);
            saveMessage(conversation, demoAnswer, "ASSISTANT");
            return new ChatbotResponseDto(demoAnswer, true, conversation.getId());
        }

        String instructions = """
                Eres el asistente virtual de una tienda ecommerce llamada Los B€ZO$.
                Responde siempre en español, con tono cercano y útil.
                Usa el contexto de catálogo proporcionado cuando sea relevante.
                Si no sabes un dato, dilo de forma honesta y ofrece una alternativa.
                No inventes disponibilidad, precios ni descuentos fuera del contexto.
                Mantén respuestas breves, claras y orientadas a ayudar a comprar.
                """;

        // Estructura esperada por Gemini generateContent: instrucciones, contenido y configuración.
        Map<String, Object> payload = Map.of(
                "systemInstruction", Map.of(
                        "parts", List.of(Map.of("text", instructions))
                ),
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(Map.of("text", buildPrompt(cleanMessage, conversation)))
                        )
                ),
                "generationConfig", Map.of(
                        "maxOutputTokens", 450
                )
        );

        try {
            // Llamada REST directa para evitar añadir una SDK al proyecto.
            Map<String, Object> response = RestClient.create()
                    .post()
                    .uri("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s".formatted(model, apiKey))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            String answer = extractOutputText(response);
            if (!StringUtils.hasText(answer)) {
                // Caso raro: Gemini responde 200 OK, pero sin texto usable.
                String fallback = "He recibido la consulta, pero no he podido interpretar la respuesta de la IA.";
                saveMessage(conversation, fallback, "ASSISTANT");
                return new ChatbotResponseDto(fallback, false, conversation.getId());
            }

            String cleanAnswer = answer.trim();
            saveMessage(conversation, cleanAnswer, "ASSISTANT");
            return new ChatbotResponseDto(cleanAnswer, false, conversation.getId());
        } catch (RestClientResponseException exception) {
            // Errores HTTP de Gemini: clave inválida, cuota, modelo no encontrado, etc.
            String error = buildGeminiErrorMessage(exception);
            saveMessage(conversation, error, "ASSISTANT");
            return new ChatbotResponseDto(error, true, conversation.getId());
        } catch (RestClientException exception) {
            // Errores de red o problemas al conectar con Google.
            String error = "Ahora mismo no puedo conectar con Gemini. Puedes probar de nuevo en unos segundos o revisar la clave GEMINI_API_KEY.";
            saveMessage(conversation, error, "ASSISTANT");
            return new ChatbotResponseDto(error, true, conversation.getId());
        }
    }

    // Prompt final: catálogo real + historial reciente + pregunta actual.
    private String buildPrompt(String message, Conversation conversation) {
        return """
                Catálogo disponible:
                %s

                Historial reciente:
                %s

                Pregunta del cliente:
                %s
                """.formatted(buildProductContext(), buildConversationContext(conversation), message);
    }

    // Convierte productos activos de la base de datos en contexto textual para la IA.
    private String buildProductContext() {
        List<Product> products = productRepository.findByAvailableTrue().stream()
                .limit(PRODUCT_CONTEXT_LIMIT)
                .toList();

        if (products.isEmpty()) {
            return "No hay productos disponibles en este momento.";
        }

        return products.stream()
                .map(this::formatProduct)
                .collect(Collectors.joining("\n"));
    }

    // Da memoria corta a Gemini para que pueda continuar la conversación.
    private String buildConversationContext(Conversation conversation) {
        List<Message> recentMessages = messageRepository.findTop12ByConversationOrderBySentAtDesc(conversation).stream()
                .sorted(Comparator.comparing(Message::getSentAt))
                .toList();

        if (recentMessages.isEmpty()) {
            return "Todavía no hay historial previo.";
        }

        return recentMessages.stream()
                .map(message -> "%s: %s".formatted(message.getSender(), message.getContent()))
                .collect(Collectors.joining("\n"));
    }

    // Formato compacto para que la IA pueda comparar precio, marca, descuento y stock.
    private String formatProduct(Product product) {
        String brand = product.getBrand() != null ? product.getBrand().getName() : "Sin marca";
        String description = StringUtils.hasText(product.getShortDescription()) ? product.getShortDescription() : "Sin descripción corta";
        double finalPrice = product.getFinalPrice() != null ? product.getFinalPrice() : 0.0;
        int discount = product.getDiscountPercentage() != null ? product.getDiscountPercentage() : 0;

        return "- %s | Marca: %s | Precio: %.2f € | Descuento: %d%% | Stock: %d | %s"
                .formatted(product.getTitle(), brand, finalPrice, discount, product.getStock(), description);
    }

    // Extrae el texto de la respuesta JSON de Gemini sin depender de clases específicas de Jackson.
    private String extractOutputText(Map<String, Object> response) {
        if (response == null) {
            return "";
        }

        Object candidates = response.get("candidates");
        if (!(candidates instanceof List<?> candidateItems)) {
            return "";
        }

        StringBuilder text = new StringBuilder();
        for (Object candidate : candidateItems) {
            if (!(candidate instanceof Map<?, ?> candidateMap)) {
                continue;
            }

            Object content = candidateMap.get("content");
            if (!(content instanceof Map<?, ?> contentMap)) {
                continue;
            }

            Object parts = contentMap.get("parts");
            if (!(parts instanceof List<?> partItems)) {
                continue;
            }

            for (Object part : partItems) {
                if (!(part instanceof Map<?, ?> partMap)) {
                    continue;
                }

                Object contentText = partMap.get("text");
                if (contentText instanceof String value && StringUtils.hasText(value)) {
                    if (!text.isEmpty()) {
                        text.append("\n");
                    }
                    text.append(value);
                }
            }
        }

        return text.toString();
    }

    // Respuestas locales para que el chatbot sea presentable aunque no haya clave configurada.
    private String buildDemoAnswer(String message) {
        String lowerMessage = message.toLowerCase(Locale.ROOT);

        if (lowerMessage.contains("oferta") || lowerMessage.contains("descuento")) {
            return "Estoy en modo demo porque falta GEMINI_API_KEY. Aun así, puedo sugerirte revisar los productos con descuento del carrusel de ofertas y comparar precio final, marca y stock.";
        }

        if (lowerMessage.contains("envío") || lowerMessage.contains("envio")) {
            return "Estoy en modo demo porque falta GEMINI_API_KEY. Para la presentación, puedo explicar que el asistente ayuda con dudas de compra, productos, stock y recomendaciones.";
        }

        return "Estoy en modo demo porque falta GEMINI_API_KEY. Cuando configures la clave, responderé con IA usando el catálogo real de la tienda como contexto.";
    }

    // Traduce códigos HTTP de Gemini a mensajes entendibles para depurar en clase.
    private String buildGeminiErrorMessage(RestClientResponseException exception) {
        int statusCode = exception.getStatusCode().value();

        return switch (statusCode) {
            case 400 -> "Gemini rechazó la petición. Revisa el modelo configurado en GEMINI_MODEL o los parámetros enviados.";
            case 401 -> "Gemini rechazó la clave. Crea una nueva en Google AI Studio y arranca la app con GEMINI_API_KEY.";
            case 403 -> "La clave de Gemini existe, pero no tiene permisos para usar este modelo o proyecto.";
            case 404 -> "Gemini no encontró el modelo configurado. Prueba con GEMINI_MODEL=gemini-2.5-flash.";
            case 429 -> "Gemini respondió con límite o cuota agotada. Espera unos minutos o revisa los límites del proyecto.";
            default -> "Gemini respondió con error HTTP %d. Revisa la configuración de la clave y el proyecto.".formatted(statusCode);
        };
    }

    // Reutiliza una conversación existente o crea una nueva si el navegador no trae conversationId.
    private Conversation resolveConversation(UUID conversationId, User user, String firstMessage) {
        if (conversationId != null) {
            Optional<Conversation> existingConversation = user == null
                    ? conversationRepository.findById(conversationId)
                    : conversationRepository.findByIdAndUser(conversationId, user);

            if (existingConversation.isPresent()) {
                Conversation conversation = existingConversation.get();
                conversation.setUpdatedAt(LocalDateTime.now());
                return conversationRepository.save(conversation);
            }
        }

        Conversation conversation = new Conversation();
        conversation.setUser(user);
        conversation.setTitle(buildTitle(firstMessage));
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    // Guarda cada turno del chat en la base de datos y actualiza la fecha de la conversación.
    private void saveMessage(Conversation conversation, String content, String sender) {
        Message message = new Message();
        message.setConversation(conversation);
        message.setContent(content);
        message.setSender(sender);
        message.setSentAt(LocalDateTime.now());
        message.setIsRead(false);
        messageRepository.save(message);

        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
    }

    // Si el usuario está logueado, vinculamos la conversación a su cuenta.
    private Optional<User> findUser(String username) {
        if (!StringUtils.hasText(username)) {
            return Optional.empty();
        }

        return userService.findByUsername(username);
    }

    // Primer mensaje como título legible para la lista de historial.
    private String buildTitle(String message) {
        String title = sanitize(message);
        if (title.length() <= 50) {
            return title;
        }

        return title.substring(0, 47) + "...";
    }

    // Limpia espacios extra y evita nulos antes de guardar o enviar texto a la IA.
    private String sanitize(String message) {
        return message == null ? "" : message.trim().replaceAll("\\s+", " ");
    }
}