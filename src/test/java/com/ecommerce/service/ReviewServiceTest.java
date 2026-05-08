package com.ecommerce.service;

import com.ecommerce.model.*;
import com.ecommerce.model.enums.*;
import com.ecommerce.repository.PurchaseRepository;
import com.ecommerce.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    
    @Mock
    private PurchaseRepository purchaseRepository;
    
    @InjectMocks
    private ReviewService reviewService;

    private User user1;
    private User user2;
    private Product product1;
    private Review review1;
    private Purchase purchase1;
    private PurchaseLine purchaseLine1;

    @BeforeEach
    void setUp() {
        // Crear usuarios
        user1 = User.builder()
                .id(UUID.randomUUID())
                .name("User 1")
                .email("user1@test.com")
                .role(Role.CUSTOMER)
                .build();
                
        user2 = User.builder()
                .id(UUID.randomUUID())
                .name("User 2")
                .email("user2@test.com")
                .role(Role.CUSTOMER)
                .build();

        // Crear producto
        product1 = Product.builder()
                .id(UUID.randomUUID())
                .title("Test Product")
                .price(100.0)
                .build();

        // Crear compra para user1
        purchase1 = Purchase.builder()
                .id(UUID.randomUUID())
                .user(user1)
                .creationDate(LocalDateTime.now())
                .purchaseStatus(PurchaseStatus.FINISHED)
                .build();

        // Crear línea de compra
        purchaseLine1 = PurchaseLine.builder()
                .id(UUID.randomUUID())
                .purchase(purchase1)
                .product(product1)
                .quantity(1)
                .build();

        // Crear review base
        review1 = Review.builder()
                .id(UUID.randomUUID())
                .title("Test Review")
                .message("Great product!")
                .rating(5)
                .user(user1)
                .product(product1)
                .build();
    }

    @Test
    void createReview_usuarioQueComprado_verificaTrue() {
        // Given - usuario que ha comprado el producto
        when(purchaseRepository.existsByUsersIdAndProductId(user1.getId(), product1.getId()))
                .thenReturn(true);
        when(reviewRepository.save(any(Review.class)))
                .thenReturn(review1);

        // When
        Review result = reviewService.createReview(review1);

        // Then
        verify(purchaseRepository).existsByUsersIdAndProductId(user1.getId(), product1.getId());
        verify(reviewRepository).save(review1);
        assertTrue(result.getVerified());
        assertEquals(ReviewStatus.PENDING_APPROVAL, result.getStatus());
        assertNotNull(result.getCreationDate());
        assertNotNull(result.getModifiedDate());
    }

    @Test
    void createReview_usuarioQueNoComprado_verificaFalse() {
        // Given - usuario que NO ha comprado el producto
        when(purchaseRepository.existsByUsersIdAndProductId(user2.getId(), product1.getId()))
                .thenReturn(false);


        Review reviewUser2 = Review.builder()
                .title("Test Review User 2")
                .message("Good product!")
                .rating(4)
                .user(user2)
                .product(product1)
                .build();

        when(reviewRepository.save(any(Review.class)))
                .thenReturn(reviewUser2);

        // When
        Review result = reviewService.createReview(reviewUser2);

        // Then
        verify(purchaseRepository).existsByUsersIdAndProductId(user2.getId(), product1.getId());
        verify(reviewRepository).save(reviewUser2);
        assertFalse(result.getVerified());
        assertEquals(ReviewStatus.PENDING_APPROVAL, result.getStatus());
        assertNotNull(result.getCreationDate());
        assertNotNull(result.getModifiedDate());
    }

    @Test
    void createReview_conStatusNull_asignaPendingApproval() {
        // Given
        review1.setStatus(null);
        when(purchaseRepository.existsByUsersIdAndProductId(any(), any()))
                .thenReturn(false);
        when(reviewRepository.save(any(Review.class)))
                .thenReturn(review1);

        // When
        Review result = reviewService.createReview(review1);

        // Then
        assertEquals(ReviewStatus.PENDING_APPROVAL, result.getStatus());
    }

    @Test
    void createReview_conVerifiedNull_asignaVerificacionSegunCompra() {
        // Given - usuario que compró
        review1.setVerified(null);
        when(purchaseRepository.existsByUsersIdAndProductId(user1.getId(), product1.getId()))
                .thenReturn(true);
        when(reviewRepository.save(any(Review.class)))
                .thenReturn(review1);

        // When
        Review result = reviewService.createReview(review1);

        // Then
        assertTrue(result.getVerified());
    }

    @Test
    void getAllReviews_devuelveTodas() {
        // Given
        List<Review> expectedReviews = List.of(review1);
        when(reviewRepository.findAll()).thenReturn(expectedReviews);

        // When
        List<Review> result = reviewService.getAllReviews();

        // Then
        assertEquals(expectedReviews, result);
        verify(reviewRepository).findAll();
    }

    @Test
    void getApprovedReviews_soloDevuelveAprobadas() {
        // Given
        Review approvedReview = Review.builder()
                .status(ReviewStatus.APPROVED)
                .build();
        Review pendingReview = Review.builder()
                .status(ReviewStatus.PENDING_APPROVAL)
                .build();
        Review rejectedReview = Review.builder()
                .status(ReviewStatus.REJECTED)
                .build();
        
        List<Review> allReviews = List.of(approvedReview, pendingReview, rejectedReview);
        when(reviewRepository.findAll()).thenReturn(allReviews);

        // When
        List<Review> result = reviewService.getApprovedReviews();

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains(approvedReview));
        assertFalse(result.contains(pendingReview));
        assertFalse(result.contains(rejectedReview));
    }

    @Test
    void getReviewsByProduct_devuelvePorProducto() {
        // Given
        List<Review> expectedReviews = List.of(review1);
        when(reviewRepository.findByProductId(product1.getId())).thenReturn(expectedReviews);

        // When
        List<Review> result = reviewService.getReviewsByProduct(product1.getId());

        // Then
        assertEquals(expectedReviews, result);
        verify(reviewRepository).findByProductId(product1.getId());
    }

    @Test
    void getApprovedReviewsByProduct_soloDevuelveAprobadasPorProducto() {
        // Given
        Review approvedReview = Review.builder()
                .status(ReviewStatus.APPROVED)
                .product(product1)
                .build();
        Review pendingReview = Review.builder()
                .status(ReviewStatus.PENDING_APPROVAL)
                .product(product1)
                .build();
        
        List<Review> allProductReviews = List.of(approvedReview, pendingReview);
        when(reviewRepository.findByProductId(product1.getId())).thenReturn(allProductReviews);

        // When
        List<Review> result = reviewService.getApprovedReviewsByProduct(product1.getId());

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains(approvedReview));
        assertFalse(result.contains(pendingReview));
    }

    @Test
    void approveReview_apruebaCorrectamente() {
        // Given
        when(reviewRepository.findById(review1.getId())).thenReturn(Optional.of(review1));
        when(reviewRepository.save(any(Review.class))).thenReturn(review1);

        // When
        Review result = reviewService.approveReview(review1.getId());

        // Then
        verify(reviewRepository).findById(review1.getId());
        verify(reviewRepository).save(review1);
        assertEquals(ReviewStatus.APPROVED, result.getStatus());
        assertNotNull(result.getModifiedDate());
    }

    @Test
    void rejectReview_rechazaCorrectamente() {
        // Given
        when(reviewRepository.findById(review1.getId())).thenReturn(Optional.of(review1));
        when(reviewRepository.save(any(Review.class))).thenReturn(review1);

        // When
        Review result = reviewService.rejectReview(review1.getId());

        // Then
        verify(reviewRepository).findById(review1.getId());
        verify(reviewRepository).save(review1);
        assertEquals(ReviewStatus.REJECTED, result.getStatus());
        assertNotNull(result.getModifiedDate());
    }

    @Test
    void deleteReview_eliminaCorrectamente() {
        // Given
        when(reviewRepository.findById(review1.getId())).thenReturn(Optional.of(review1));

        // When
        reviewService.deleteReview(review1.getId());

        // Then
        verify(reviewRepository).findById(review1.getId());
        verify(reviewRepository).delete(review1);
    }

    @Test
    void getReviewsByRating_devuelvePorRating() {
        // Given
        List<Review> expectedReviews = List.of(review1);
        when(reviewRepository.findByRating(5)).thenReturn(expectedReviews);

        // When
        List<Review> result = reviewService.getReviewsByRating(5);

        // Then
        assertEquals(expectedReviews, result);
        verify(reviewRepository).findByRating(5);
    }

    @Test
    void getPendingReviews_devuelvePendientes() {
        // Given
        Review pendingReview1 = Review.builder()
                .status(ReviewStatus.PENDING_APPROVAL)
                .build();
        Review approvedReview = Review.builder()
                .status(ReviewStatus.APPROVED)
                .build();
        Review pendingReview2 = Review.builder()
                .status(ReviewStatus.PENDING_APPROVAL)
                .build();
        
        List<Review> allReviews = List.of(pendingReview1, approvedReview, pendingReview2);
        when(reviewRepository.findAll()).thenReturn(allReviews);

        // When
        List<Review> result = reviewService.getPendingReviews();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(pendingReview1));
        assertTrue(result.contains(pendingReview2));
        assertFalse(result.contains(approvedReview));
    }

    @Test
    void approveReview_conReviewNoExistente_lanzaException() {
        // Given
        when(reviewRepository.findById(review1.getId())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> reviewService.approveReview(review1.getId()));
        
        assertEquals("Review not found with id: " + review1.getId(), exception.getMessage());
    }

    @Test
    void rejectReview_conReviewNoExistente_lanzaException() {
        // Given
        when(reviewRepository.findById(review1.getId())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> reviewService.rejectReview(review1.getId()));
        
        assertEquals("Review not found with id: " + review1.getId(), exception.getMessage());
    }

    @Test
    void deleteReview_conReviewNoExistente_lanzaException() {
        // Given
        when(reviewRepository.findById(review1.getId())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> reviewService.deleteReview(review1.getId()));
        
        assertEquals("Review not found with id: " + review1.getId(), exception.getMessage());
    }
}
