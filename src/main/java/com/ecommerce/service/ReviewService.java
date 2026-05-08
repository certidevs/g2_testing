package com.ecommerce.service;

import com.ecommerce.model.Review;
import com.ecommerce.model.enums.ReviewStatus;
import com.ecommerce.repository.PurchaseRepository;
import com.ecommerce.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PurchaseRepository purchaseRepository;

    /**
     * Crea una nueva reseña con la fecha de creación actual
     * y estado PENDING_APPROVAL por defecto.
     * La verificación depende de si el usuario ha comprado el producto.
     *
     * @param review objeto Review con los datos de la reseña
     * @return reseña guardada con fecha de creación, estado y verificación asignados
     */
    public Review createReview(Review review) {
        review.setCreationDate(LocalDateTime.now());
        review.setModifiedDate(LocalDateTime.now());
        
        // Estado por defecto: pendiente de aprobación
        if (review.getStatus() == null) {
            review.setStatus(ReviewStatus.PENDING_APPROVAL);
        }
        
        // Verificación: solo si el usuario ha comprado el producto
        boolean hasPurchased = false;
        if (review.getUser() != null && review.getProduct() != null) {
            hasPurchased = purchaseRepository.existsByUsersIdAndProductId(
                review.getUser().getId(),
                review.getProduct().getId()
            );
        }
        
        if (review.getVerified() == null) {
            review.setVerified(hasPurchased);
        }
        
        return reviewRepository.save(review);
    }

    /**
     * Obtiene todas las reseñas de la base de datos (para admin).
     *
     * @return lista de todas las reseñas
     */
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    /**
     * Obtiene solo las reseñas aprobadas (para vistas públicas).
     *
     * @return lista de reseñas aprobadas
     */
    public List<Review> getApprovedReviews() {
        return reviewRepository.findAll().stream()
                .filter(review -> ReviewStatus.APPROVED.equals(review.getStatus()))
                .toList();
    }

    /**
     * Busca una reseña por su ID.
     *
     * @param id UUID de la reseña
     * @return Optional con la reseña encontrada o vacío si no existe
     */
    public Optional<Review> getReviewById(UUID id) {
        return reviewRepository.findById(id);
    }

    
    /**
     * Elimina una reseña por su ID.
     *
     * @param id UUID de la reseña a eliminar
     * @throws RuntimeException si la reseña no existe
     */
    public void deleteReview(UUID id) {
        Review review = findReviewEntityById(id);
        reviewRepository.delete(review);
    }

    /**
     * Obtiene todas las reseñas de un producto específico (para admin).
     *
     * @param productId UUID del producto
     * @return lista de reseñas del producto
     */
    public List<Review> getReviewsByProduct(UUID productId) {
        return reviewRepository.findByProductId(productId);
    }

    /**
     * Obtiene solo las reseñas aprobadas de un producto específico (para vistas públicas).
     *
     * @param productId UUID del producto
     * @return lista de reseñas aprobadas del producto
     */
    public List<Review> getApprovedReviewsByProduct(UUID productId) {
        return reviewRepository.findByProductId(productId).stream()
                .filter(review -> ReviewStatus.APPROVED.equals(review.getStatus()))
                .toList();
    }

    /**
     * Filtra reseñas por valoración (rating).
     *
     * @param rating valoración del 1 al 5
     * @return lista de reseñas con esa valoración
     */
    public List<Review> getReviewsByRating(Integer rating) {
        return reviewRepository.findByRating(rating);
    }

    /**
     * Obtiene todas las reseñas ordenadas de mejor a peor valoración.
     *
     * @return lista de reseñas ordenadas por rating descendente
     */
    public List<Review> getReviewsBestRated() {
        return reviewRepository.findAllByOrderByRatingDesc();
    }

    /**
     * Obtiene todas las reseñas ordenadas de peor a mejor valoración.
     *
     * @return lista de reseñas ordenadas por rating ascendente
     */
    public List<Review> getReviewsWorstRated() {
        return reviewRepository.findAllByOrderByRatingAsc();
    }

    /**
     * Filtra reseñas por rango de fechas de creación.
     *
     * @param startDate fecha de inicio
     * @param endDate fecha de fin
     * @return lista de reseñas en ese rango de fechas
     */
    public List<Review> getReviewsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return reviewRepository.findByCreationDateBetween(startDate, endDate);
    }

    /**
     * Filtra reseñas verificadas o no verificadas.
     *
     * @param verified true para verificadas, false para no verificadas
     * @return lista de reseñas según estado de verificación
     */
    public List<Review> getReviewsByVerificationStatus(Boolean verified) {
        return reviewRepository.findByVerified(verified);
    }

    /**
     * Cuenta el número total de reseñas de un producto.
     *
     * @param productId UUID del producto
     * @return número de reseñas del producto
     */
    public Long countReviewsByProduct(UUID productId) {
        return reviewRepository.countByProductId(productId);
    }

    /**
     * Aprueba una reseña cambiando su estado a APPROVED.
     * Solo los administradores pueden aprobar reseñas.
     *
     * @param id UUID de la reseña a aprobar
     * @return reseña aprobada
     * @throws RuntimeException si la reseña no existe
     */
    public Review approveReview(UUID id) {
        Review review = findReviewEntityById(id);
        review.setStatus(ReviewStatus.APPROVED);
        review.setModifiedDate(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    /**
     * Rechaza una reseña cambiando su estado a REJECTED.
     * Solo los administradores pueden rechazar reseñas.
     *
     * @param id UUID de la reseña a rechazar
     * @return reseña rechazada
     * @throws RuntimeException si la reseña no existe
     */
    public Review rejectReview(UUID id) {
        Review review = findReviewEntityById(id);
        review.setStatus(ReviewStatus.REJECTED);
        review.setModifiedDate(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    /**
     * Marca una reseña como verificada.
     *
     * @param id UUID de la reseña a verificar
     * @return reseña verificada
     * @throws RuntimeException si la reseña no existe
     */
    public Review verifyReview(UUID id) {
        Review review = findReviewEntityById(id);
        review.setVerified(true);
        review.setModifiedDate(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    /**
     * Obtiene reseñas pendientes de aprobación.
     *
     * @return lista de reseñas con estado PENDING_APPROVAL
     */
    public List<Review> getPendingReviews() {
        return reviewRepository.findAll().stream()
                .filter(review -> ReviewStatus.PENDING_APPROVAL.equals(review.getStatus()))
                .toList();
    }

    /**
     * Recupera una entidad Review por ID o lanza excepción.
     * Método auxiliar para validaciones internas.
     *
     * @param id UUID de la reseña
     * @return entidad Review existente
     * @throws RuntimeException si la reseña no existe
     */
    private Review findReviewEntityById(UUID id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
    }
}
