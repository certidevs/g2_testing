package com.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.model.Users;
import com.ecommerce.model.enums.Gender;
import com.ecommerce.model.enums.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<Users, UUID> {
    // Consultas personalizadas

    // Traer usuario por name
    List<Users> findByName(String name);

        
    // Traer usuario por email
    List<Users> findByEmail(String email);

    // Traer usuario por telefono
    List<Users> findByPhone(String phone);

    // Traer usuario por genero
    List<Users> findByGender(Gender gender);

    // Traer usuario por role
    List<Users> findByRole(Role role);

    // Traer usuario por fecha de creacion
    List<Users> findByCreationDateBetween(LocalDateTime creationDateAfter, LocalDateTime creationDateBefore);
}
