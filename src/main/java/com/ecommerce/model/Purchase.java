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
    private User user;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private java.util.List<PurchaseLine> lines = new java.util.ArrayList<>();

    // Returns the purchase lines as an array, if there are no lines, it returns an empty array
    public PurchaseLine[] getPurchaseLines() {
        if (this.lines == null) {
            return new PurchaseLine[0];
        }
        return this.lines.toArray(new PurchaseLine[0]);
    }

    // Establishes the total amount of the purchase by summing the total of each line (price * quantity)
    public void setTotalAmount(double total) {
        this.totalPrice = total;
    }

    // Establishes the purchase date to the current date and time when the purchase is created
    public void setPurchaseDate(LocalDateTime now) {
        this.creationDate = now;
    }

    // Method to add purchase lines
    public void addLine(PurchaseLine line) {
        if (this.lines == null) this.lines = new ArrayList<>();
        this.lines.add(line);
        line.setPurchase(this);
    }
}