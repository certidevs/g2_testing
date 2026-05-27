package com.ecommerce.repository;

import com.ecommerce.model.Conversation;
import com.ecommerce.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    // Últimos turnos usados como memoria corta para que Gemini entienda el contexto.
    List<Message> findTop12ByConversationOrderBySentAtDesc(Conversation conversation);
}
