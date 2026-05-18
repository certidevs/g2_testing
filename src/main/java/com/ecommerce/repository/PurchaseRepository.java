package com.ecommerce.repository;

import com.ecommerce.model.enums.*;
import com.ecommerce.model.Purchase;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {

    // -------- SIMPLES --------

    // Specific user
    List<Purchase> findByUserId(UUID users_id);

    // Purchase status
    List<Purchase> findByPurchaseStatus(PurchaseStatus purchaseStatus);

    // Payment status
    List<Purchase> findByPaymentStatus(PaymentStatus paymentStatus);

    // Process status
    List<Purchase> findByProcessStatus(ProcessStatus processStatus);

    // Shipping status
    List<Purchase> findByShippingStatus(ShippingStatus shippingStatus);

    // Shipping mode
    List<Purchase> findByShippingMode(ShippingMode shippingMode);

    // --------- RANGES --------

    List<Purchase> findByCreationDateBetween(LocalDateTime creationDateAfter, LocalDateTime creationDateBefore);

    // Finished data between
    List<Purchase> findByFinishedDateBetween(LocalDateTime finishedDateAfter, LocalDateTime finishedDateBefore);

    // Total price between
    List<Purchase> findByTotalPriceBetween(Double minTotalPrice, Double maxTotalPrice);

    // Total price greater than a value
    List<Purchase> findByTotalPriceGreaterThan(Double totalPrice);

    // Total price less than a value
    List<Purchase> findByTotalPriceLessThan(Double totalPrice);

    // -------- SPECIFICS --------

    // Specific purchase & user comment (key word)
    List<Purchase> findByIdAndUserCommentContaining(UUID product_id, String userComment);

    // Specific user & user comment (key word)
    List<Purchase> findByUserIdAndUserCommentContaining(UUID user_id, String userComment);

    // User comment (key word)
    List<Purchase> findByUserCommentContaining(String keyword);

    // -------- ORDER --------

    // Sort by creation date descendent
    List<Purchase> findAllByOrderByCreationDateDesc();

    // Sort by creation date ascendant
    List<Purchase> findAllByOrderByCreationDateAsc();

    // -------- COMPLEX --------

    // Purchases finished on range date and sort by total price
    List<Purchase> findByFinishedDateBetweenAndPurchaseStatusOrderByTotalPrice(
            LocalDateTime start, LocalDateTime end, PurchaseStatus status);

     // Specific purchase & shipping mode
    List<Purchase> findByIdAndShippingMode(UUID id, ShippingMode shippingMode);

    // -------- METHODS --------

    // Check if user has purchased a specific product through PurchaseLine
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM Purchase p " +
           "JOIN p.lines pl " +
           "JOIN pl.product pr " +
           "WHERE p.user.id = :userId AND pr.id = :productId")
    boolean existsByUsersIdAndProductId(@Param("userId") UUID userId, @Param("productId") UUID productId);

    Optional<Purchase> findFirstByPurchaseStatus(PurchaseStatus purchaseStatus);

    //List<Purchase> findByUserIdAndUserCommentContaining(UUID userId, String userComment);
}
