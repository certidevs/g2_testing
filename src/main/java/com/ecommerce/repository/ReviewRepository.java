package com.ecommerce.repository;

import com.ecommerce.model.Reviews;
//import com.ecommers.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Reviews, Long> {
    // Conultas personalizadas

    // Traer review por producto
    List<Reviews> findByProductId(UUID productId);

    //Filtrar reviews por valoración
    List<Reviews> findByRating(Integer rating);

    // Ordenar reviews de mejor a peor
    List<Reviews> findAllByOrderByRatingDesc();

    // Ordenar reviews de peor a mejor
    List<Reviews> findAllByOrderByRatingAsc();

    // Filtrar reviews por fecha de creación
    List<Reviews> findByCreationDateBetween(LocalDateTime creationDateAfter, LocalDateTime creationDateBefore);

    // Filtar por reseñas verificadas
    List<Reviews> findByVerified(Boolean verified);

    // Contar numero total de reseñas de cada producto
    Long countByProductId(UUID productId);

    // Filtrar mejores reviews por categoría
    // List<Reviews> findByProductCategory(Category category);


}
