package com.ecommerce.model;

import com.ecommerce.model.enums.PurchaseStatus;
import com.ecommerce.model.enums.ShippingMode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table (name = "purchases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // ID del producto comprado

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now(); // Fecha y hora de inicio de la compra


    private LocalDateTime finishedDate; // Fecha y hora de finalización de la compra

    @Builder.Default
    @Enumerated(EnumType.STRING) // Estado de la compra: INICIADO, INACTIVO, TERMINADO
    private PurchaseStatus purchaseStatus = PurchaseStatus.INICIADO;

    @Enumerated(EnumType.STRING) // Tipo de envío: STANDARD, EXPRESS, PREMIUM
    private ShippingMode shippingMode;

    private Double totalPrice; // Precio total de compra

    private String userComment; // Requisitos especificados por el comprador a la hora de la entrega

//    @PrePersist // Esto garantiza valores por defecto al crear el registro de la compra
//    public void prePersist()
//    {
//        this.creationDate = LocalDateTime.now();
//    }
//
//    @PreUpdate //Para llevar un registro de la ultima modificación.
//    public void preUpdate() {
//        if(purchaseStatus == PurchaseStatus.TERMINADO){
//            this.finishedDate = LocalDateTime.now(); //Actualiza la fecha de finalización si el estado es TERMINADO
//        }
//    }
}