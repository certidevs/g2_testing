package com.ecommers.repository;

import com.ecommers.models.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Reviews, Long> {
    // Conultas personalizadas

    // Traer review por producto
    List<Reviews> findByProductId(Long productId);

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
    List<Reviews> countByProductId(Long productId);

    // Filtrar mejores reviews por categoría
    List<Reviews> findByCategory(String category);
}
