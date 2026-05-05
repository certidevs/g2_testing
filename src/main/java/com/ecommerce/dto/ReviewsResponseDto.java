package com.ecommerce.dto;

import com.ecommerce.model.enums.ReviewStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewsResponseDto {

    private UUID id;
    private String title;
    private String message;
    private Integer rating;
    private Boolean verified;
    private ReviewStatus status;
    private LocalDateTime creationDate;
    private LocalDateTime modifiedDate;

    // Solo el id y nombre del producto, no el objeto completo
    private UUID productId;
    private String productTitle;

    // Solo el id y nombre del usuario, no el objeto completo
    private UUID usersId;
    private String usersName;
}

