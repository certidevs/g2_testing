package com.ecommerce.service;

import com.ecommerce.model.Purchase;
import com.ecommerce.model.PurchaseLine;
import com.ecommerce.repository.PurchaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PurchaseService {

     private final PurchaseRepository purchaseRepository;

    // Creates de purchase with all its lines, calculates the total and saves it to the database
    // @param purchase object that contains the purchase data and its lines
    // @return Purchase saves the purchase with its lines and total amount calculated
    public Purchase createPurchase(Purchase purchase) {

        double total = 0.0; // Initialize total amount to 0

        for (PurchaseLine line : purchase.getPurchaseLines()) { // Iterate through each line in the purchase

            line.setPurchase(purchase); // Established relation between purchase and line
            double lineTotal = line.getPrice() * line.getQuantity(); // Calculate line total (price * quantity)
            total += lineTotal;
        }

        purchase.setTotalAmount(total); // Save the total amount of the purchase

        purchase.setPurchaseDate(LocalDateTime.now()); // Set the purchase date to the current date and time

        return purchaseRepository.save(purchase); // Save the purchase with its lines and total amount calculated
    }

    // Returns all purchases in the database, if there are no purchases, it returns an empty list
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    // Returns a purchase by ID, if the purchase does not exist, it returns an empty Optional
    public Optional<Purchase> getPurchaseById(UUID id) {
        return purchaseRepository.findById(id);
    }

    // Deletes a purchase by ID, if the purchase does not exist, it does nothing
    public void deletePurchase(UUID id) {
        purchaseRepository.deleteById(id);
    }
}