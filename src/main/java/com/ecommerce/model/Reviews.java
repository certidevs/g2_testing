package com.ecommerce.model;


import com.ecommerce.model.enums.ReviewStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;
import java.time.LocalDateTime;
import com.ecommerce.model.Category;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table (name = "reviews")
@AllArgsConstructor
@Builder
@ToString(exclude = {"product"})
public class Reviews {

    // Reviews  uuid
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    // Creation date
    private LocalDateTime creationDate;

    // Modified date
    @Builder.Default
    private LocalDateTime modifiedDate = LocalDateTime.now();

    // Product
    @ManyToOne
    private Product product;

    // Review status
    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    // User
    @ManyToOne
    private Users users;

    // Listado de reviews para admin para aprobar o desaprobar con estados
}
