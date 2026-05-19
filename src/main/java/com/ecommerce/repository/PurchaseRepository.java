package com.ecommerce.repository;

import com.ecommerce.model.enums.*;
import com.ecommerce.model.Purchase;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {

    // -------- SIMPLES --------

    // Usuario específico
    List<Purchase> findByUserId(UUID users_id);

    // Estado de compra
    List<Purchase> findByPurchaseStatus(PurchaseStatus purchaseStatus);

    // Estado de pago
    List<Purchase> findByPaymentStatus(PaymentStatus paymentStatus);

    // Estado de proceso
    List<Purchase> findByProcessStatus(ProcessStatus processStatus);

    // Estado de envío
    List<Purchase> findByShippingStatus(ShippingStatus shippingStatus);

    // Modo de envío
    List<Purchase> findByShippingMode(ShippingMode shippingMode);

    // --------- RANGOS --------

    // Compras creadas entre dos fechas
    List<Purchase> findByCreationDateBetween(LocalDateTime creationDateAfter, LocalDateTime creationDateBefore);

    // Compras finalizadas entre dos fechas
    List<Purchase> findByFinishedDateBetween(LocalDateTime finishedDateAfter, LocalDateTime finishedDateBefore);

    // Compras con precio total entre dos valores
    List<Purchase> findByTotalPriceBetween(Double minTotalPrice, Double maxTotalPrice);

    // Compras con precio total mayor a un valor
    List<Purchase> findByTotalPriceGreaterThan(Double totalPrice);

    // Compras con precio total menor a un valor
    List<Purchase> findByTotalPriceLessThan(Double totalPrice);

    // -------- ESPECÍFICOS --------

    // Compras de un producto específico y comentario del usuario (key word)
    List<Purchase> findByIdAndUserCommentContaining(UUID product_id, String userComment);

    // Usuario específico y comentario del usuario (key word)
    List<Purchase> findByUserIdAndUserCommentContaining(UUID user_id, String userComment);

    // Comentario del usuario (key word)
    List<Purchase> findByUserCommentContaining(String keyword);

    // -------- ORDEN --------

    // Ordenar por fecha de creación descendente
    List<Purchase> findAllByOrderByCreationDateDesc();

    // Ordenar por fecha de creación ascendente
    List<Purchase> findAllByOrderByCreationDateAsc();

    // -------- COMPLEJOS --------

    // Compras terminadas entre dos fechas y con un estado de compra específico, ordenadas por precio total
    List<Purchase> findByFinishedDateBetweenAndPurchaseStatusOrderByTotalPrice(
            LocalDateTime start, LocalDateTime end, PurchaseStatus status);

    // Compra especifica con modo de envío específico
    List<Purchase> findByIdAndShippingMode(UUID id, ShippingMode shippingMode);

    // -------- FUNCTIONS --------

    // Comprueba si existe una compra de un producto específico para un usuario específico, devuelve true si existe, false si no existe
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Purchase p " +
            "JOIN p.lines pl " +
            "JOIN pl.product pr " +
            "WHERE p.user.id = :userId AND pr.id = :productId")
    boolean existsByUsersIdAndProductId(@Param("userId") UUID userId, @Param("productId") UUID productId);

    // Busca el primer carrito iniciado, sin importar el usuario, para mostrarlo en el carrito de compras, si no hay ningún carrito iniciado, devuelve un Optional vacío
    Optional<Purchase> findFirstByPurchaseStatus(PurchaseStatus purchaseStatus);

    // Busca el primer carrito iniciado que pertenezca estrictamente al usuario actual
    Optional<Purchase> findFirstByUserIdAndPurchaseStatus(UUID userId, PurchaseStatus purchaseStatus);
}