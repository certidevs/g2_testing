package com.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
@Table(name = "messages")
public class Message {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        // Texto literal enviado por el usuario o devuelto por Gemini.
        @Column(nullable = false, length = 1000)
        private String content;

        // USER o ASSISTANT, para pintar cada burbuja a un lado distinto.
        @Column(nullable = false, length = 20)
        private String sender;

        // Momento en el que se guarda el mensaje.
        @Column(nullable = false)
        private LocalDateTime sentAt;

        private Boolean isRead = false;

        // Conversación a la que pertenece este mensaje.
        @ManyToOne
        @JoinColumn(name = "conversation_id")
        private Conversation conversation;
}
