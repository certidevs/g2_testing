package com.ecommerce.repository;

import com.ecommerce.model.PurchaseLine;
import com.ecommerce.model.Purchase;
import com.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PurchaseLineRepository extends JpaRepository<PurchaseLine, UUID> {

    // -------- SIMPLES --------

    // Specific purchase
    List<PurchaseLine> findByPurchase(Purchase purchase);

    // Specific product
    List<PurchaseLine> findByProduct(Product product);

    // --------- RANGE --------

    // Quantity greater than a value
    List<PurchaseLine> findByQuantityGreaterThan(int quantity);

    // Quantity less than a value
    List<PurchaseLine> findByQuantityLessThan(int quantity);

    // Quantity range
    List<PurchaseLine> findByQuantityBetween(int minQuantity, int maxQuantity);

    // -------- ORDER --------

    // Sort by quantity in descending order
    List<PurchaseLine> findByPurchaseOrderByQuantityDesc(Purchase purchase);

    // Sort by quantity in ascending order
    List<PurchaseLine> findByPurchaseOrderByQuantityAsc(Purchase purchase);

    // -------- COMPLEX --------

    // Purchase & product
    List<PurchaseLine> findByPurchaseAndProduct(Purchase purchase, Product product);

    // Product & quantity
    List<PurchaseLine> findByProductAndQuantity(Product product, int quantity);

    // Product & quantity greater than a value
    List<PurchaseLine> findByProductAndQuantityGreaterThan(Product product, int quantity);

    // Product & quantity less than a value
    List<PurchaseLine> findByProductAndQuantityLessThan(Product product, int quantity);

    // Product & quantity between values
    List<PurchaseLine> findByProductAndQuantityBetween(Product product, int minQuantity, int maxQuantity);
}
