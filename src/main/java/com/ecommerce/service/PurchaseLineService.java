package com.ecommerce.service;

import com.ecommerce.model.PurchaseLine;
import com.ecommerce.model.Purchase;
import com.ecommerce.repository.PurchaseLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseLineService {

    private final PurchaseLineRepository purchaseLineRepository;

    // Get all purchase lines
    public List<PurchaseLine> getAllPurchaseLines() {
        return purchaseLineRepository.findAll();
    }

    // Get purchase line by ID
    public Optional<PurchaseLine> getPurchaseLineById(UUID id) {
        return purchaseLineRepository.findById(id);
    }

    // Helper to get purchase line or throw exception
    private PurchaseLine getPurchaseLineEntityById(UUID id) {
        return purchaseLineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PurchaseLine not found with id: " + id));
    }

    // Create purchase line
    @Transactional
    public PurchaseLine createPurchaseLine(PurchaseLine purchaseLine) {
        return purchaseLineRepository.save(purchaseLine);
    }

    // Update purchase line
    @Transactional
    public PurchaseLine updatePurchaseLine(UUID id, PurchaseLine purchaseLineDetails) {
        PurchaseLine line = getPurchaseLineEntityById(id);
        if (purchaseLineDetails.getQuantity() > 0)
            line.setQuantity(purchaseLineDetails.getQuantity());
        if (purchaseLineDetails.getProduct() != null)
            line.setProduct(purchaseLineDetails.getProduct());
        if (purchaseLineDetails.getPurchase() != null)
            line.setPurchase(purchaseLineDetails.getPurchase());
        return purchaseLineRepository.save(line);
    }

    // Update quantity of purchase line
    @Transactional
    public PurchaseLine updateQuantity(UUID id, int newQuantity) {
        PurchaseLine line = getPurchaseLineEntityById(id);
        line.setQuantity(newQuantity);
        return purchaseLineRepository.save(line);
    }

    // Delete purchase line by ID
    @Transactional
    public void deletePurchaseLine(UUID id) {
        purchaseLineRepository.deleteById(id);
    }

    // Get purchase lines by purchase
    public List<PurchaseLine> getPurchaseLinesByPurchase(Purchase purchase) {
        return purchaseLineRepository.findByPurchase(purchase);
    }
}