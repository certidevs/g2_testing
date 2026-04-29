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
    private UUID id; // Id de la línea de compre

    private int quantity; // Cantidad del producto comprado

    @ToString.Exclude
    @ManyToOne
    Purchase purchase; // Compra a la que pertenece la línea de compra

    @ToString.Exclude
    @ManyToOne
    Product product; // Producto comprado
}
