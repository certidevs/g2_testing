package com.ecommerce.repository;

import com.ecommerce.model.enums.PurchaseStatus;
import com.ecommerce.model.enums.ShippingMode;
import com.ecommerce.model.Product;
import com.ecommerce.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    // -------- SIMPLES --------

    // Status
    List<Purchase> findByPurchaseStatus(PurchaseStatus purchaseStatus);

    // Modo de envío
    List<Purchase> findByShippingMode(ShippingMode shippingMode);

    // Producto asociado
    List<Purchase> findByProduct(Product product);

    // --------- RANGOS --------

    // Rango de fecha de creación
    List<Purchase> findByCreationDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Rango de fecha de finalización
    List<Purchase> findByFinishedDateBetween(LocalDateTime finishedDateAfter, LocalDateTime finishedDateBefore);

    // Rango precio unitario
    List<Purchase> findByUnitPriceBetween(Double minPrice, Double maxPrice);

    // Rango precio total
    List<Purchase> findByTotalPriceBetween(Double minTotalPrice, Double maxTotalPrice);

    // Precio total mayor a un valor
    List<Purchase> findByTotalPriceGreaterThan(Double totalPrice);

    // Precio total menor a un valor
    List<Purchase> finByTotalPriceLessThan(Double totalPrice);

    // -------- ESPECÍFICOS --------

    // Comentario del usuario (palabra clave)
    List<Purchase> findByUserCommentContaining(String keyword);

    // -------- ORDENACIÓN --------

    // Ordenado por fecha de creación descendente
    List<Purchase> findAllByOrderByCreationDateDesc();

    // -------- COMPLEJOS --------

    // Compras terminadas en rango de fechas y ordenadas por precio total
    List<Purchase> findByFinishedDateBetweenAndPurchaseStatusOrderByTotalPrice(
            LocalDateTime start, LocalDateTime end, PurchaseStatus status);

}
