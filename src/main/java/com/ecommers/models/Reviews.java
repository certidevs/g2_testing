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

    // Review's really verificated?
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

    // Vamos a integrar una aprobación de la review? es decir, en función de su status
    // Listado de reviews para admin para aprobar o desaprobar con estados

}
