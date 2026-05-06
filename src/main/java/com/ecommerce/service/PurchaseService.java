package com.ecommerce.service;

import com.ecommerce.model.Purchase;
import com.ecommerce.model.PurchaseLine;
import com.ecommerce.model.enums.*;
import com.ecommerce.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    // Create purchase with automatic total calculation
    public Purchase createPurchase(Purchase purchase) {
        double total = 0.0;
        for (PurchaseLine line : purchase.getPurchaseLines()) {
            line.setPurchase(purchase);
            total += line.getPrice() * line.getQuantity();
        }
        purchase.setTotalAmount(total);
        purchase.setPurchaseDate(LocalDateTime.now());
        return purchaseRepository.save(purchase);
    }

    // Get all purchases
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    // Get purchase by ID
    public Optional<Purchase> getPurchaseById(UUID id) {
        return purchaseRepository.findById(id);
    }

    // Helper to get purchase or throw exception
    private Purchase getPurchaseEntityById(UUID id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found with id: " + id));
    }

    // Update purchase
    public Purchase updatePurchase(UUID id, Purchase purchaseDetails) {
        Purchase purchase = getPurchaseEntityById(id);
        if (purchaseDetails.getUserComment() != null)
            purchase.setUserComment(purchaseDetails.getUserComment());
        if (purchaseDetails.getPurchaseStatus() != null)
            purchase.setPurchaseStatus(purchaseDetails.getPurchaseStatus());
        if (purchaseDetails.getPaymentStatus() != null)
            purchase.setPaymentStatus(purchaseDetails.getPaymentStatus());
        if (purchaseDetails.getProcessStatus() != null)
            purchase.setProcessStatus(purchaseDetails.getProcessStatus());
        if (purchaseDetails.getShippingStatus() != null)
            purchase.setShippingStatus(purchaseDetails.getShippingStatus());
        if (purchaseDetails.getShippingMode() != null)
            purchase.setShippingMode(purchaseDetails.getShippingMode());
        if (purchaseDetails.getFinishedDate() != null)
            purchase.setFinishedDate(purchaseDetails.getFinishedDate());
        return purchaseRepository.save(purchase);
    }

    // Delete purchase by ID
    public void deletePurchase(UUID id) {
        purchaseRepository.deleteById(id);
    }

    // Get purchases by specific user
    public List<Purchase> getPurchasesByUserId(UUID userId) {
        return purchaseRepository.findByUserId(userId);
    }

    // Filter by purchase status
    public List<Purchase> getPurchasesByPurchaseStatus(PurchaseStatus status) {
        return purchaseRepository.findByPurchaseStatus(status);
    }

    // Filter by payment status
    public List<Purchase> getPurchasesByPaymentStatus(PaymentStatus status) {
        return purchaseRepository.findByPaymentStatus(status);
    }

    // Filter by process status
    public List<Purchase> getPurchasesByProcessStatus(ProcessStatus status) {
        return purchaseRepository.findByProcessStatus(status);
    }

    // Filter by shipping status
    public List<Purchase> getPurchasesByShippingStatus(ShippingStatus status) {
        return purchaseRepository.findByShippingStatus(status);
    }

    // Filter by shipping mode
    public List<Purchase> getPurchasesByShippingMode(ShippingMode mode) {
        return purchaseRepository.findByShippingMode(mode);
    }

    // Purchases in creation date range
    public List<Purchase> getPurchasesByCreationDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return purchaseRepository.findByCreationDateBetween(startDate, endDate);
    }

    // Purchases in finished date range
    public List<Purchase> getPurchasesByFinishedDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return purchaseRepository.findByFinishedDateBetween(startDate, endDate);
    }

    // Purchases in price range
    public List<Purchase> getPurchasesByTotalPriceRange(Double minPrice, Double maxPrice) {
        return purchaseRepository.findByTotalPriceBetween(minPrice, maxPrice);
    }

    // Purchases with price greater than
    public List<Purchase> getPurchasesByTotalPriceGreaterThan(Double price) {
        return purchaseRepository.findByTotalPriceGreaterThan(price);
    }

    // Purchases with price less than
    public List<Purchase> getPurchasesByTotalPriceLessThan(Double price) {
        return purchaseRepository.findByTotalPriceLessThan(price);
    }

    // Search by user comment
    public List<Purchase> getPurchasesByUserComment(String keyword) {
        return purchaseRepository.findByUserCommentContaining(keyword);
    }

    // Search specific purchase with comment
    public List<Purchase> getPurchasesByIdAndUserComment(UUID id, String userComment) {
        return purchaseRepository.findByIdAndUserCommentContaining(id, userComment);
    }

    public List<Purchase> getPurchasesByUserIdAndComment(UUID userId, String userComment) {
        return purchaseRepository.findByUserIdAndUserCommentContaining(userId, userComment);
    }

    // Order by creation date descending
    public List<Purchase> getPurchasesOrderByCreationDateDesc() {
        return purchaseRepository.findAllByOrderByCreationDateDesc();
    }

    // Order by creation date ascending
    public List<Purchase> getPurchasesOrderByCreationDateAsc() {
        return purchaseRepository.findAllByOrderByCreationDateAsc();
    }

    // Finished purchases in range with specific status, sorted by price
    public List<Purchase> getPurchasesByFinishedDateRangeAndStatus(
            LocalDateTime startDate, LocalDateTime endDate, PurchaseStatus status) {
        return purchaseRepository.findByFinishedDateBetweenAndPurchaseStatusOrderByTotalPrice(startDate, endDate, status);
    }

    // Specific purchase with shipping mode
    public List<Purchase> getPurchasesByIdAndShippingMode(UUID id, ShippingMode shippingMode) {
        return purchaseRepository.findByIdAndShippingMode(id, shippingMode);
    }

    // Update purchase status
    public Purchase updatePurchaseStatus(UUID id, PurchaseStatus status) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setPurchaseStatus(status);
        return purchaseRepository.save(purchase);
    }

    // Update payment status
    public Purchase updatePaymentStatus(UUID id, PaymentStatus status) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setPaymentStatus(status);
        return purchaseRepository.save(purchase);
    }

    // Update process status
    public Purchase updateProcessStatus(UUID id, ProcessStatus status) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setProcessStatus(status);
        return purchaseRepository.save(purchase);
    }

    // Update shipping status
    public Purchase updateShippingStatus(UUID id, ShippingStatus status) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setShippingStatus(status);
        return purchaseRepository.save(purchase);
    }

    // Update shipping mode
    public Purchase updateShippingMode(UUID id, ShippingMode mode) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setShippingMode(mode);
        return purchaseRepository.save(purchase);
    }

    // Update user comment
    public Purchase updateUserComment(UUID id, String comment) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setUserComment(comment);
        return purchaseRepository.save(purchase);
    }

    // Mark purchase as finished
    public Purchase finishPurchase(UUID id) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setFinishedDate(LocalDateTime.now());
        purchase.setPurchaseStatus(PurchaseStatus.FINISHED);
        return purchaseRepository.save(purchase);
    }
}