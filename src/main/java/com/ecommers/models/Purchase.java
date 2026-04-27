package com.ecommers.models;

import com.ecommers.enums.PurchaseStatus;
import com.ecommers.enums.ShippingMode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table (name = "Compras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID productId; // ID del producto comprado

    private LocalDateTime creationDate; // Fecha y hora de inicio de la compra

    private LocalDateTime finishedDate; // Fecha y hora de finalización de la compra

    @Enumerated(EnumType.STRING) // Estado de la compra: INICIADO, INACTIVO, TERMINADO
    private PurchaseStatus purchaseStatus;

    @Enumerated(EnumType.STRING) // Tipo de envío: STANDARD, EXPRESS, PREMIUM
    private ShippingMode shippingMode;

    private Double unitPrice; // Precio por unidad

    private Double totalPrice; // Precio total de compra

    private String userComment; // Requisitos especificados por el comprador a la hora de la entrega

    // @ToString.Exclude
    // @ManyToOne
    // private PurchaseLine purchaseLine;
}