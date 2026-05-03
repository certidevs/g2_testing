package com.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table (name = "purchase_line")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseLine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // ID Purchase line

    private int quantity; // Quantity of product purchased

    @ToString.Exclude
    @ManyToOne
    Purchase purchase; // Purchase to which the purchase line belongs

    @ToString.Exclude
    @ManyToOne
    Product product; // Product purchased
}
