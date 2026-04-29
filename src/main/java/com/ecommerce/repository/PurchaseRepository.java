package com.ecommerce.repository;

import com.ecommerce.model.enums.PurchaseStatus;
import com.ecommerce.model.enums.ShippingMode;
import com.ecommerce.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    // LATER: Buscar compra asociada a Id de User **[SIMPLE]**
    // LATER: Buscar compras asociadas a un producto concreto y comentario del usuario (palabra clave) **[COMPLEX]**

    // -------- SIMPLES --------

    // Status
    List<Purchase> findByPurchaseStatus(PurchaseStatus purchaseStatus);

    // Modo de envío
    List<Purchase> findByShippingMode(ShippingMode shippingMode);

    // --------- RANGES --------

    List<Purchase> findByCreationDateBetween(LocalDateTime creationDateAfter, LocalDateTime creationDateBefore);

    // Rango de fecha de finalización
    List<Purchase> findByFinishedDateBetween(LocalDateTime finishedDateAfter, LocalDateTime finishedDateBefore);

    // Rango precio total
    List<Purchase> findByTotalPriceBetween(Double minTotalPrice, Double maxTotalPrice);

    // Precio total mayor a un valor
    List<Purchase> findByTotalPriceGreaterThan(Double totalPrice);

    // Precio total menor a un valor
    List<Purchase> findByTotalPriceLessThan(Double totalPrice);

    // -------- SPECIFICS --------

    // Comentario del usuario (palabra clave)
    List<Purchase> findByUserCommentContaining(String keyword);

    // -------- ORDER --------

    // Ordenado por fecha de creación descendente
    List<Purchase> findAllByOrderByCreationDateDesc();

    // Ordenado por fecha de creación ascendente¿?
    List<Purchase> findAllByOrderByCreationDateAsc();

    // -------- COMPLEX --------

    // Compras terminadas en rango de fechas y ordenadas por precio total
    List<Purchase> findByFinishedDateBetweenAndPurchaseStatusOrderByTotalPrice(
            LocalDateTime start, LocalDateTime end, PurchaseStatus status);
}
