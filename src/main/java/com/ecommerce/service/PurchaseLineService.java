package com.ecommerce.service;

import com.ecommerce.model.PurchaseLine;
import com.ecommerce.repository.PurchaseLineRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PurchaseLineService {

    private final PurchaseLineRepository purchaseLineRepository;

    // Returns all purchase lines in the database, if there are no purchase lines, it returns an empty list
    public List<PurchaseLine> getAllPurchaseLines() {
        return purchaseLineRepository.findAll();
    }

    // Searches for a purchase line by its ID, if the purchase line does not exist, it returns an empty Optional
    public Optional<PurchaseLine> getPurchaseLineById(UUID id) {
        return purchaseLineRepository.findById(id);
    }

    // Updates the quantity of a purchase line.
    @Transactional // I put @Transactional to ensure data integrity.
    public PurchaseLine updateQuantity(UUID id, int newQuantity) {
        // I use 'map' to find the purchase line by ID
        return purchaseLineRepository.findById(id).map(line -> {
            line.setQuantity(newQuantity);  // If it exists then It update its quantity
            return purchaseLineRepository.save(line); // and save it.
        }).orElseThrow(() -> new RuntimeException("PurchaseLine not found with id: " + id)); // If it does not exist, I throw an exception.
    }

    // Deletes a purchase line by ID, if the purchase line does not exist, it does nothing
    @Transactional
    public void deletePurchaseLine(UUID id) {
        purchaseLineRepository.deleteById(id);
    }
}
