package com.ecommerce.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandResponseDto
{
    private UUID id;
    private String name;
    private String country;
    private String website;
    private Boolean active;
}
