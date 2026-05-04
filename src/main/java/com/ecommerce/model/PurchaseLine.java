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
    @JoinColumn(name = "purchase_id")
    @ManyToOne
    Purchase purchase; // Purchase to which the purchase line belongs

    @ToString.Exclude
    @JoinColumn(name = "product_id")
    @ManyToOne
    Product product; // Product purchased

    // Returns the price of the product, if the product does not exist, it returns 0.0
    public double getPrice() {
        return product.getPrice();
    }
}
