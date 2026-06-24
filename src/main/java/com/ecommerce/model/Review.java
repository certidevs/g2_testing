package com.ecommerce.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;
import java.time.LocalDateTime;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table (name = "reviews")
@AllArgsConstructor
@Builder
@ToString(exclude = {"product"})
public class Review {

    // Review  uuid
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Integer rating 1-5
    @Min(1)
    @Max(5)
    private Integer rating;

    // Review titulo
    @Column(length = 1000)
    private String title;

    // Review mensaje
    @Column(length = 1000)
    private String message;


    // Creación tiempo
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();



    // Product
    //muchas reviews para un producto
    @ManyToOne
    private Product product;


    // User
    // muchos reviews para un usuario
    @ManyToOne
    private User user;

}
