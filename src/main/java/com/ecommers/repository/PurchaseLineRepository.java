package com.ecommers.repository;

import com.ecommers.models.PurchaseLine;
import com.ecommers.models.Purchase;
import com.ecommers.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PurchaseLineRepository extends JpaRepository<PurchaseLine, UUID> {

    // -------- SIMPLES --------

    // Compra concreta
    List<PurchaseLine> findByPurchase(Purchase purchase);

    // Producto concreto
    List<PurchaseLine> findByProduct(Product product);

    // --------- RANGOS --------

    // Cantidad mayor a un valor
    List<PurchaseLine> findByQuatintyGreaterThan(int quantity);

    // Cantidad menor a un valor
    List<PurchaseLine> findByQuatintyLessThan(int quantity);

    // Rango de cantidad
    List<PurchaseLine> findByQuatintyBetween(int minQuantity, int maxQuantity);

    // -------- ORDENACIÓN --------

    // Compras ordenadas por cantidad descendente
    List<PurchaseLine> findByPurchaseOrderByQuatintyDesc(Purchase purchase);

    // -------- COMPLEJOS --------

    // Compra y producto
    List<PurchaseLine> findByPurchaseAndProduct(Purchase purchase, Product product);


}
