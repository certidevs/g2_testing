package com.ecommerce.model;

import com.ecommerce.model.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@Table (name = "purchases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // ID de la compra

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now(); // Fecha y hora de creación de la compra

    private LocalDateTime finishedDate; // Fecha y hora de finalización de la compra

    @Builder.Default
    @Enumerated(EnumType.STRING) // Estatus de la compra: INITIATED (iniciado), INACTIVE (inactivo), FINISHED (finalizado)
    private PurchaseStatus purchaseStatus = PurchaseStatus.INITIATED;

    @Enumerated(EnumType.STRING) // Modo de envío: STANDARD (estándar), EXPRESS (express), PREMIUM (premium)
    private ShippingMode shippingMode;

    @Enumerated(EnumType.STRING) // Estado del envío: PENDING (pendiente), SHIPPED (enviado), IN_TRANSIT (en tránsito), OUT_FOR_DELIVERY (EN REPARTO), DELIVERED (entregado)
    private ShippingStatus shippingStatus;

    @Enumerated(EnumType.STRING) // Payment status: PENDING (pendiente), PAID (pagado), FAILED (fallido)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING) // Process status: PROCESSING (procesando), ON_HOLD (en espera), COMPLETED (completado), CANCELLED (cancelado)
    private ProcessStatus processStatus;

    private Double totalPrice; // Precio total de la compra, calculado a partir de las líneas de compra (price * quantity)

    private String userComment; // Comentario opcional del usuario para la entrega de la compra

    // Asociación con el usuario que realizó la compra, una compra pertenece a un solo usuario, pero un usuario puede tener varias compras
    @ManyToOne
    private User user;

    // Asociación con las líneas de compra, una compra puede tener varias líneas, cada línea representa un producto comprado con su cantidad y precio
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private java.util.List<PurchaseLine> lines = new java.util.ArrayList<>();

    // Función para obtener las líneas de compra como un array, si no hay líneas devuelve un array vacío
    public PurchaseLine[] getPurchaseLines() {
        if (this.lines == null) {
            return new PurchaseLine[0];
        }
        return this.lines.toArray(new PurchaseLine[0]);
    }

    // Establece el precio total de la compra sumando el precio de cada línea de compra (price * quantity)
    public void setTotalAmount(double total) {
        this.totalPrice = total;
    }

    // Establece la fecha de compra a la fecha y hora actual, se puede llamar al crear una nueva compra o al finalizarla
    public void setPurchaseDate(LocalDateTime now) {
        this.creationDate = now;
    }
}