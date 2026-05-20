package com.ecommerce.repository;

import com.ecommerce.model.Review;
import com.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    // Conultas personalizadas
    List<Review>findByProduct_IdOrderByCreationDateDesc( UUID id);

    List<Review> findByProductId(UUID id);
}
