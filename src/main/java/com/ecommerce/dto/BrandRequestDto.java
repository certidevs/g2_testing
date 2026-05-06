package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// Lo que el cliente manda para crear/editar una marca.
public class BrandRequestDto
{
    @NotBlank(message = "El nombre de la marca es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    @NotBlank(message = "El NIF es obligatorio")
    @Size(min = 9, max = 9, message = "El NIF debe tener 9 caracteres")
    private String nif;

    @Size(max = 100, message = "EL país no puede superar 100 caracteres")
    private String country;

    @Size(max = 255, message = "La web no puede superar los 255 caracteres")
    private String website;

    @Size(max = 255, message = "El logo no puede superar los 255 caracteres")
    private String logo;

    private Boolean active;
}
