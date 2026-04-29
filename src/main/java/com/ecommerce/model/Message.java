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

        @Column(nullable = false, length = 1000)
        private String content;

        @Column(nullable = false)
        private LocalDateTime sentAt;

        private Boolean isRead = false;
        @ManyToOne
        @JoinColumn(name = "conversation_id")
        private Conversation conversation;
}
