package com.ecommerce.repository;

import com.ecommerce.model.enums.*;
import com.ecommerce.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    // -------- SIMPLES --------

    // Specific user
    List<Purchase> findByUserId(UUID users_id);

    // Specific purchase
    Optional<Purchase> findById(UUID purchaseId);

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
    List<Purchase> findByIdAndUserCommentContaining(UUID id, String userComment);

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

    // Method to delete a purchase by ID, if the purchase does not exist, it does nothing
    default void deleteById(UUID id){
        if (findById(id).isPresent()) {
            deleteById(id);
        }
    }

    //List<Purchase> findByUserIdAndUserCommentContaining(UUID userId, String userComment);
}
