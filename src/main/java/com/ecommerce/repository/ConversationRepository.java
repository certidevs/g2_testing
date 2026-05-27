package com.ecommerce.repository;

import com.ecommerce.model.Conversation;
import com.ecommerce.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    // EntityGraph trae los mensajes junto con la conversación porque open-in-view está desactivado.
    @EntityGraph(attributePaths = "messages")
    List<Conversation> findByUserOrderByUpdatedAtDesc(User user);

    // Evita que un usuario pueda consultar una conversación que pertenece a otra cuenta.
    @EntityGraph(attributePaths = "messages")
    Optional<Conversation> findByIdAndUser(UUID id, User user);
}
