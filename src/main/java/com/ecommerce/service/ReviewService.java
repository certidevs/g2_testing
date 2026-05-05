package com.ecommerce.service;

import com.ecommerce.model.Reviews;
import com.ecommerce.model.enums.ReviewStatus;
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

    /**
     * Crea una nueva reseña con la fecha de creación actual
     * y estado PENDING_APPROVAL por defecto.
     *
     * @param review objeto Reviews con los datos de la reseña
     * @return reseña guardada con fecha de creación y estado asignados
     */
    public Reviews createReview(Reviews review) {
        review.setCreationDate(LocalDateTime.now());
        review.setModifiedDate(LocalDateTime.now());
        
        if (review.getStatus() == null) {
            review.setStatus(ReviewStatus.PENDING_APPROVAL);
        }
        
        if (review.getVerified() == null) {
            review.setVerified(false);
        }
        
        return reviewRepository.save(review);
    }

    /**
     * Obtiene todas las reseñas de la base de datos.
     *
     * @return lista de todas las reseñas
     */
    public List<Reviews> getAllReviews() {
        return reviewRepository.findAll();
    }

    /**
     * Busca una reseña por su ID.
     *
     * @param id UUID de la reseña
     * @return Optional con la reseña encontrada o vacío si no existe
     */
    public Optional<Reviews> getReviewById(UUID id) {
        return reviewRepository.findById(id);
    }

    
    /**
     * Elimina una reseña por su ID.
     *
     * @param id UUID de la reseña a eliminar
     * @throws RuntimeException si la reseña no existe
     */
    public void deleteReview(UUID id) {
        Reviews review = findReviewEntityById(id);
        reviewRepository.delete(review);
    }

    /**
     * Obtiene todas las reseñas de un producto específico.
     *
     * @param productId UUID del producto
     * @return lista de reseñas del producto
     */
    public List<Reviews> getReviewsByProduct(UUID productId) {
        return reviewRepository.findByProductId(productId);
    }

    /**
     * Filtra reseñas por valoración (rating).
     *
     * @param rating valoración del 1 al 5
     * @return lista de reseñas con esa valoración
     */
    public List<Reviews> getReviewsByRating(Integer rating) {
        return reviewRepository.findByRating(rating);
    }

    /**
     * Obtiene todas las reseñas ordenadas de mejor a peor valoración.
     *
     * @return lista de reseñas ordenadas por rating descendente
     */
    public List<Reviews> getReviewsBestRated() {
        return reviewRepository.findAllByOrderByRatingDesc();
    }

    /**
     * Obtiene todas las reseñas ordenadas de peor a mejor valoración.
     *
     * @return lista de reseñas ordenadas por rating ascendente
     */
    public List<Reviews> getReviewsWorstRated() {
        return reviewRepository.findAllByOrderByRatingAsc();
    }

    /**
     * Filtra reseñas por rango de fechas de creación.
     *
     * @param startDate fecha de inicio
     * @param endDate fecha de fin
     * @return lista de reseñas en ese rango de fechas
     */
    public List<Reviews> getReviewsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return reviewRepository.findByCreationDateBetween(startDate, endDate);
    }

    /**
     * Filtra reseñas verificadas o no verificadas.
     *
     * @param verified true para verificadas, false para no verificadas
     * @return lista de reseñas según estado de verificación
     */
    public List<Reviews> getReviewsByVerificationStatus(Boolean verified) {
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
     *
     * @param id UUID de la reseña a aprobar
     * @return reseña aprobada
     * @throws RuntimeException si la reseña no existe
     */
    public Reviews approveReview(UUID id) {
        Reviews review = findReviewEntityById(id);
        review.setStatus(ReviewStatus.APPROVED);
        review.setModifiedDate(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    /**
     * Rechaza una reseña cambiando su estado a REJECTED.
     *
     * @param id UUID de la reseña a rechazar
     * @return reseña rechazada
     * @throws RuntimeException si la reseña no existe
     */
    public Reviews rejectReview(UUID id) {
        Reviews review = findReviewEntityById(id);
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
    public Reviews verifyReview(UUID id) {
        Reviews review = findReviewEntityById(id);
        review.setVerified(true);
        review.setModifiedDate(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    /**
     * Obtiene reseñas pendientes de aprobación.
     *
     * @return lista de reseñas con estado PENDING_APPROVAL
     */
    public List<Reviews> getPendingReviews() {
        return reviewRepository.findAll().stream()
                .filter(review -> ReviewStatus.PENDING_APPROVAL.equals(review.getStatus()))
                .toList();
    }

    /**
     * Recupera una entidad Reviews por ID o lanza excepción.
     * Método auxiliar para validaciones internas.
     *
     * @param id UUID de la reseña
     * @return entidad Reviews existente
     * @throws RuntimeException si la reseña no existe
     */
    private Reviews findReviewEntityById(UUID id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
    }
}
