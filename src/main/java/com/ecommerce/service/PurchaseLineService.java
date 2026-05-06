package com.ecommerce.service;

import com.ecommerce.model.PurchaseLine;
import com.ecommerce.model.Purchase;
import com.ecommerce.model.Product;
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

    // Get purchase lines by purchase ID
    public List<PurchaseLine> getPurchaseLinesByPurchaseId(UUID purchaseId) {
        return purchaseLineRepository.findByPurchaseId(purchaseId);
    }

    // Get purchase lines by product
    public List<PurchaseLine> getPurchaseLinesByProduct(Product product) {
        return purchaseLineRepository.findByProduct(product);
    }

    // Get purchase lines with quantity greater than
    public List<PurchaseLine> getPurchaseLinesByQuantityGreaterThan(int quantity) {
        return purchaseLineRepository.findByQuantityGreaterThan(quantity);
    }

    // Get purchase lines with quantity less than
    public List<PurchaseLine> getPurchaseLinesByQuantityLessThan(int quantity) {
        return purchaseLineRepository.findByQuantityLessThan(quantity);
    }

    // Get purchase lines in quantity range
    public List<PurchaseLine> getPurchaseLinesByQuantityRange(int minQuantity, int maxQuantity) {
        return purchaseLineRepository.findByQuantityBetween(minQuantity, maxQuantity);
    }

    // Order purchase lines by quantity descending
    public List<PurchaseLine> getPurchaseLinesByPurchaseOrderByQuantityDesc(Purchase purchase) {
        return purchaseLineRepository.findByPurchaseOrderByQuantityDesc(purchase);
    }

    // Order purchase lines by quantity ascending
    public List<PurchaseLine> getPurchaseLinesByPurchaseOrderByQuantityAsc(Purchase purchase) {
        return purchaseLineRepository.findByPurchaseOrderByQuantityAsc(purchase);
    }

    // Get purchase lines by purchase and product
    public List<PurchaseLine> getPurchaseLinesByPurchaseAndProduct(Purchase purchase, Product product) {
        return purchaseLineRepository.findByPurchaseAndProduct(purchase, product);
    }

    // Get purchase lines by product and quantity
    public List<PurchaseLine> getPurchaseLinesByProductAndQuantity(Product product, int quantity) {
        return purchaseLineRepository.findByProductAndQuantity(product, quantity);
    }

    // Get purchase lines by product and quantity greater than
    public List<PurchaseLine> getPurchaseLinesByProductAndQuantityGreaterThan(Product product, int quantity) {
        return purchaseLineRepository.findByProductAndQuantityGreaterThan(product, quantity);
    }

    // Get purchase lines by product and quantity less than
    public List<PurchaseLine> getPurchaseLinesByProductAndQuantityLessThan(Product product, int quantity) {
        return purchaseLineRepository.findByProductAndQuantityLessThan(product, quantity);
    }

    // Get purchase lines by product and quantity range
    public List<PurchaseLine> getPurchaseLinesByProductAndQuantityRange(Product product, int minQuantity, int maxQuantity) {
        return purchaseLineRepository.findByProductAndQuantityBetween(product, minQuantity, maxQuantity);
    }
}