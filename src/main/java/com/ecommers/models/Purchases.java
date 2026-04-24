package com.ecommers.models;

import com.ecommers.enums.PurchaseStatus;
import com.ecommers.enums.ShippingMode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purchases {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId; // ID del producto comprado

    private LocalDateTime creationDate; // Fecha y hora de inicio de la compra

    private LocalDateTime finishedDate; // Fecha y hora de finalización de la compra

    @Enumerated(EnumType.STRING) // Estado de la compra: INICIADO, INACTIVO, TERMINADO
    private PurchaseStatus purchaseStatus;

    @Enumerated(EnumType.STRING) // Tipo de envío: STANDARD, EXPRESS, PREMIUM
    private ShippingMode shippingMode;

    private Double unitPrice; // Precio por unidad

    private Double totalPrice; // Precio total de compra

    private String userComment; // Requisitos especificados por el comprador a la hora de la entrega

    @ToString.Exclude
    @ManyToOne
    private Product product; // Asociación con el producto comprado

    // @ToString.Exclude
    // @ManyToOne
    // private PurchaseLine purchaseLine;

}