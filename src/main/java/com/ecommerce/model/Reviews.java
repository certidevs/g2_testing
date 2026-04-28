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
@ToString(exclude = {"product","category"})
public class Reviews {

    // Reviews  uuid
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    // Integer rating 1-5
    @Min(1)
    @Max(5)
    private Integer rating;

    // Review's title
    @Column(length = 1000)

    private String title;
    @Column(length = 1000)
    // Review's message
    private String message;

    // Review's really verificated?
    private Boolean verified;

    // Review's product id
    private UUID productId;

    // Creation date
    private LocalDateTime creationDate;

    // Modified date
    @Builder.Default
    private LocalDateTime modifiedDate = LocalDateTime.now();

    // Product
    @ManyToOne
    private Product product;

    // Tostring para categories
    // @ToString.Exclude
    // @ManyToOne
    // private Category category;

    // Vamos a integrar una aprobación de la review? es decir, en función de su status
    // Listado de reviews para admin para aprobar o desaprobar con estados
    // Estados de la reseña:approved, pending approval, rejected
}
