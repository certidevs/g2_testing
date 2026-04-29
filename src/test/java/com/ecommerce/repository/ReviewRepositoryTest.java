package com.ecommerce.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import com.ecommers.models.Reviews;
import com.ecommers.models.Product;
import com.ecommers.repository.ProductRepository;

@DataJpaTest
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    // @Autowired
    // Private CategoryRepository categoryRepository

    Product product1;
    Product product2;
    Reviews review1;
    Reviews review2;

    
    @Test
    void findByProductId() {
        // Given
        // When
        // Then
    }

    @Test
    void findByRating() {
    }

    @Test
    void findAllByOrderByRatingDesc() {
    }

    @Test
    void findAllByOrderByRatingAsc() {
    }

    @Test
    void findByCreationDateBetween() {
    }

    @Test
    void findByVerified() {
    }

    @Test
    void countByProductId() {
        // Given
        // When
        // Then
    }

    @Test
    void findByProductCategory() {
    }

}
