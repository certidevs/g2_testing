package com.ecommers.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;
import java.time.LocalDateTime;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@Entity
@AllArgsConstructor
@ToString
public class Reviews {

    // Reviews  long id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Integer rating 1-5
    @Min(1)
    @Max(5)
    private Integer rating;

    // Review's message
    private String message;

    // Review's verification status
    private Boolean verified;

    // Review's product id
    private UUID productId;

    // Creation date
    private LocalDateTime creationDate;

    // Modified date
    private LocalDateTime modifiedDate;

    @ToString.Exclude
    @ManyToOne
    private Product product;

}
