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
    public void createPurchase(Purchase purchase) {
        double total = 0.0;
        for (PurchaseLine line : purchase.getPurchaseLines()) {
            line.setPurchase(purchase);
            total += line.getPrice() * line.getQuantity();
        }
        purchase.setTotalAmount(total);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchaseRepository.save(purchase);
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

    // Update purchase status [NOT USED]
    public Purchase updatePurchaseStatus(UUID id, PurchaseStatus purchaseStatus) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setPurchaseStatus(purchaseStatus);
        return purchaseRepository.save(purchase);
    }

    // Update payment status [NOT USED]
    public Purchase updatePaymentStatus(UUID id, PaymentStatus paymentStatus) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setPaymentStatus(paymentStatus);
        return purchaseRepository.save(purchase);
    }

    // Update process status [NOT USED]
    public Purchase updateProcessStatus(UUID id, ProcessStatus processStatus) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setProcessStatus(processStatus);
        return purchaseRepository.save(purchase);
    }

    // Update shipping status [NOT USED]
    public Purchase updateShippingStatus(UUID id, ShippingStatus shippingStatus) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setShippingStatus(shippingStatus);
        return purchaseRepository.save(purchase);
    }

    // Update shipping mode [NOT USED]
    public Purchase updateShippingMode(UUID id, ShippingMode shippingMode) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setShippingMode(shippingMode);
        return purchaseRepository.save(purchase);
    }

    // Update user comment [NOT USED]
    public Purchase updateUserComment(UUID id, String comment) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setUserComment(comment);
        return purchaseRepository.save(purchase);
    }

    // Mark purchase as finished [NOT USED]
    public Purchase finishPurchase(UUID id) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setPurchaseStatus(PurchaseStatus.FINISHED);
        purchase.setFinishedDate(LocalDateTime.now());
        return purchaseRepository.save(purchase);
    }
}