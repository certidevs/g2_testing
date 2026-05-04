package com.ecommerce.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// Lo que el cliente manda para crear/editar un producto.
public class BrandRequestDto
{
    private String name;
    private String country;
    private String website;
    private Boolean active;
}
