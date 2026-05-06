package com.ecommerce.repository;

import com.ecommerce.model.Review;
import com.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    // Conultas personalizadas
    static List<Review> findByProduct_idOrderByCreationDateDesc(UUID id) {
        return null;
    }

    // Traer review por producto
    List<Review> findByProductId(UUID productId);

    //Filtrar reviews por valoración
    List<Review> findByRating(Integer rating);

    // Ordenar reviews de mejor a peor
    List<Review> findAllByOrderByRatingDesc();

    // Ordenar reviews de peor a mejor
    List<Review> findAllByOrderByRatingAsc();

    // Filtrar reviews por fecha de creación
    List<Review> findByCreationDateBetween(LocalDateTime creationDateAfter, LocalDateTime creationDateBefore);

    // Filtar por reseñas verificadas
    List<Review> findByVerified(Boolean verified);

    // Contar numero total de reseñas de cada producto
    Long countByProductId(UUID productId);

    // Filtrar mejores reviews por categoría
    List<Review> findByProductSubcategory(Category category);


}
