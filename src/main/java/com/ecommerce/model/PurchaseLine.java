package com.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table (name = "Carrito")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseLine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int quatinty;

    @ToString.Exclude
    @ManyToOne
    Purchase purchase;

    @ToString.Exclude
    @ManyToOne
    Product product;
}
