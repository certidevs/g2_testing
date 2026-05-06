package com.ecommerce.repository;

import com.ecommerce.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import com.ecommerce.model.Product;
import com.ecommerce.model.Category;
import java.time.LocalDateTime;

import java.util.List;



@DataJpaTest
@EnableJpaRepositories(basePackages = "com.ecommerce.repository", 
    includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, 
    classes = {ReviewRepository.class, ProductRepository.class, CategoryRepository.class}))
public class ReviewRepositoryTest{

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // @Autowired
    // Private CategoryRepository categoryRepository

    Product product1;
    Product product2;
    Review review1;
    Review review2;

    @BeforeEach
    // Initialize data
    void setUp() {
        // Create and save category
        Category category = categoryRepository.save(Category.builder().name("Test Category").slug("test-category").build());
        
        // Create and save products with category
        product1 = productRepository.save(Product.builder().title("Product 1").subcategory(category).build());
        product2 = productRepository.save(Product.builder().title("Product 2").subcategory(category).build());

        // Define specific dates for testing
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoWeeksAgo = now.minusWeeks(2);
        LocalDateTime twoMonthsAgo = now.minusMonths(2);
        
        // Create and save reviews with varied ratings and specific dates
        review1 = reviewRepository.save(Review.builder().product(product1).rating(5).verified(true)
            .creationDate(now).build());
        review2 = reviewRepository.save(Review.builder().product(product2).rating(3).verified(false)
            .creationDate(twoWeeksAgo).build());
        
        // Create additional reviews with different ratings and dates for comprehensive tests
        Review review3 = reviewRepository.save(Review.builder().product(product1).rating(1).verified(false)
            .creationDate(now.minusDays(5)).build());
        Review review4 = reviewRepository.save(Review.builder().product(product2).rating(2).verified(true)
            .creationDate(now.minusWeeks(5)).build());
        Review review5 = reviewRepository.save(Review.builder().product(product1).rating(4).verified(false)
            .creationDate(twoMonthsAgo).build());
        Review review6 = reviewRepository.save(Review.builder().product(product2).rating(5).verified(true)
            .creationDate(now.minusDays(10)).build());
    }

    @Test
    void findByProductId() {
        // Given
        List<Review> reviews = reviewRepository.findByProductId(product1.getId());
        assertEquals(3, reviews.size());
    }

    @Test
    void findByRating() {
        // Given
        List<Review> reviews = reviewRepository.findByRating(5);
        assertEquals(2, reviews.size());
    }

    @Test
    void findAllByOrderByRatingDesc() {
        // Given
        List<Review> reviews = reviewRepository.findAllByOrderByRatingDesc();
        assertEquals(6, reviews.size());
        assertEquals(5, reviews.get(0).getRating());
        assertEquals(5, reviews.get(1).getRating());
        assertEquals(4, reviews.get(2).getRating());
        assertEquals(3, reviews.get(3).getRating());
        assertEquals(2, reviews.get(4).getRating());
        assertEquals(1, reviews.get(5).getRating());
    }

    @Test
    void findAllByOrderByRatingAsc() {
        List<Review> reviews = reviewRepository.findAllByOrderByRatingAsc();
        assertEquals(6, reviews.size());
        assertEquals(1, reviews.get(0).getRating());
        assertEquals(2, reviews.get(1).getRating());
        assertEquals(3, reviews.get(2).getRating());
        assertEquals(4, reviews.get(3).getRating());
        assertEquals(5, reviews.get(4).getRating());
        assertEquals(5, reviews.get(5).getRating());
    }

    @Test
    void findByCreationDateBetween() {
        // Given - Filter reviews from last month (should find 4 reviews)
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = LocalDateTime.now();
        
        // When
        List<Review> reviews = reviewRepository.findByCreationDateBetween(startDate, endDate);
        
        // Then - Should find reviews created within last month (review1, review2, review3, review6)
        assertEquals(4, reviews.size());
    }

    @Test
    void findByVerified() {
        // Given
        List<Review> reviews = reviewRepository.findByVerified(true);
        assertEquals(3, reviews.size());
    }

    @Test
    void countByProductId() {
        Long count = reviewRepository.countByProductId(product1.getId());
        assertEquals(3, count);
    }

    @Test
    void findByProductCategory() {
        // Given
        Category category = product1.getSubcategory();
        
        // When
        List<Review> reviews = reviewRepository.findByProductSubcategory(category);
        
        // Then - Should find all reviews for products in this category (6 reviews total)
        assertEquals(6, reviews.size());
        
        // Verify all reviews belong to products in the same category
        for (Review review : reviews) {
            assertEquals(category, review.getProduct().getSubcategory());
        }
    }

}
