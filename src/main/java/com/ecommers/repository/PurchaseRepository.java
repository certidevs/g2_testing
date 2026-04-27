package com.ecommers.repository;

import com.ecommers.enums.PurchaseStatus;
import com.ecommers.enums.ShippingMode;
import com.ecommers.models.Product;
import com.ecommers.models.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    // Simples
    // Encontrar compras por estado (ej. INICIADO, TERMINADO)
    List<Purchase> findByPurchaseStatus(PurchaseStatus purchaseStatus);

    // Encontrar compras por modo de envío
    List<Purchase> findByShippingMode(ShippingMode shippingMode);

    // Encontrar compras por rango de fecha de creación
    List<Purchase> findByCreationDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Encontrar compras por rango de fecha de finalización
    List<Purchase> findByFinishedDateBetween(LocalDateTime finishedDateAfter, LocalDateTime finishedDateBefore);

    // Encontrar compras por producto asociado
    List<Purchase> findByProduct(Product product);

    // Encontrar compras con precio total mayor a un valor
    List<Purchase> findByTotalPriceGreaterThan(Double totalPrice);

    // Encontrar compras con precio total menor a un valor
    List<Purchase> finByTotalPriceLessThan(Double totalPrice);

    // Encontrar compras con precio unitario entre dos valores
    List<Purchase> findByUnitPriceBetween(Double minPrice, Double maxPrice);

    // Encontrar compras con precio total entre dos valores
    List<Purchase> findByTotalPriceBetween(Double minTotalPrice, Double maxTotalPrice);

    // Encontrar compras por comentario del usuario (si contiene una palabra clave)
    List<Purchase> findByUserCommentContaining(String keyword);

    // Encontrar compras ordenadas por fecha de creación descendente
    List<Purchase> findAllByOrderByCreationDateDesc();

    // Encontrar compras terminadas en un rango de fechas, ordenadas por precio total
    List<Purchase> findByFinishedDateBetweenAndPurchaseStatusOrderByTotalPrice(
            LocalDateTime start, LocalDateTime end, PurchaseStatus status);

    // Encontrar por Id del producto
    List<Purchase> findByProductId(UUID productId);
}
