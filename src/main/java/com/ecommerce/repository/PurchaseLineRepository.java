package com.ecommerce.repository;

import com.ecommerce.model.PurchaseLine;
import com.ecommerce.model.Purchase;
import com.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PurchaseLineRepository extends JpaRepository<PurchaseLine, UUID> {

    // -------- SIMPLES --------

    // Compra concreta
    List<PurchaseLine> findByPurchase(Purchase purchase);

    // Producto concreto
    List<PurchaseLine> findByProduct(Product product);

    // --------- RANGE --------

    // Cantidad mayor a un valor
    List<PurchaseLine> findByQuantityGreaterThan(int quantity);

    // Cantidad menor a un valor
    List<PurchaseLine> findByQuantityLessThan(int quantity);

    // Rango de cantidad
    List<PurchaseLine> findByQuantityBetween(int minQuantity, int maxQuantity);

    // -------- ORDER --------

    // Compras ordenadas por cantidad descendente
    List<PurchaseLine> findByPurchaseOrderByQuantityDesc(Purchase purchase);

    // Compras ordenadas por cantidad descendente¿?

    // -------- COMPLEX --------

    // Compra y producto
    List<PurchaseLine> findByPurchaseAndProduct(Purchase purchase, Product product);

    // Producto y quantity¿?

    // Buscar compras con cantidad igual o mayor de un producto concreto¿?


}
