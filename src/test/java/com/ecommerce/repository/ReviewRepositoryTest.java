package com.ecommerce.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import com.ecommerce.model.Reviews;
import com.ecommerce.model.Product;

import java.util.List;



@DataJpaTest
@EnableJpaRepositories(basePackages = "com.ecommerce.repository", 
    includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, 
    classes = {ReviewRepository.class, ProductRepository.class}))
public class ReviewRepositoryTest{

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

    @BeforeEach
    // Initialize data
    void setUp() {
        // Create and save products
        product1 = productRepository.save(Product.builder().title("Product 1").build());
        product2 = productRepository.save(Product.builder().title("Product 2").build());

        // Create and save reviews
        review1 = reviewRepository.save(Reviews.builder().product(product1).rating(5).verified(true).build());
        review2 = reviewRepository.save(Reviews.builder().product(product2).rating(3).verified(false).build());
    }


    @Test
    void findByProductId() {
        // Given
        List<Reviews> reviews = reviewRepository.findByProductId(product1.getId());
        assertEquals(1, reviews.size());
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
