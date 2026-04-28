package com.ecommerce.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.ToString;

import java.util.UUID;

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
