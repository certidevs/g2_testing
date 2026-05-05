package com.ecommerce.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewsRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String message;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    @NotNull
    private UUID productId;

    @NotNull
    private UUID usersId;
}

