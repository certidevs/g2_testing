package com.ecommerce.model;

import com.ecommerce.model.enums.*;
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
    private UUID id; // ID Purchase

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now(); // Date & time the purchase started

    private LocalDateTime finishedDate; // Date & time the purchase finished

    @Builder.Default
    @Enumerated(EnumType.STRING) // Status of purchase: INITIATED INACTIVE, FINISHED
    private PurchaseStatus purchaseStatus = PurchaseStatus.INITIATED;

    @Enumerated(EnumType.STRING) // Shipping mode: STANDARD, EXPRESS, PREMIUM
    private ShippingMode shippingMode;

    @Enumerated(EnumType.STRING) // Shipping status: SHIPPED, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED
    private ShippingStatus shippingStatus;

    @Enumerated(EnumType.STRING) // Payment status: PENDING, PAID, FAILED
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING) // Process status: PROCESSING, ON_HOLD, COMPLETED, CANCELLED
    private ProcessStatus processStatus;

    private Double totalPrice; // Total purchase price

    private String userComment; // Requirements specified by the buyer at the time of delivery

    @ManyToOne
    private Users users;
}