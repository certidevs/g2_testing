package com.ecommerce.repository;

import com.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import com.ecommerce.model.enums.Gender;
import com.ecommerce.model.enums.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // Consultas personalizadas

    // Traer usuario por name
    List<User> findByName(String name);
        
    // Traer usuario por email
    List<User> findByEmail(String email);

    // Traer primer usuario por email con direcciones precargadas
    @EntityGraph(attributePaths = "addresses")
    Optional<User> findFirstByEmail(String email);

    // Traer usuario por telefono
    List<User> findByPhone(String phone);

    // Traer usuario por genero
    List<User> findByGender(Gender gender);

    // Traer usuario por role
    List<User> findByRole(Role role);

    // Traer primer usuario administrador disponible
    Optional<User> findFirstByRole(Role role);

    // Traer primer usuario disponible con direcciones precargadas
    @EntityGraph(attributePaths = "addresses")
    Optional<User> findFirstByOrderByCreationDateAsc();

    // Traer usuario por fecha de creacion
    List<User> findByCreationDateBetween(LocalDateTime creationDateAfter, LocalDateTime creationDateBefore);
}
