package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// Lo que el cliente manda para crear/editar una categoria.
public class CategoryRequestDto
{
    @NotBlank(message = "El nombre de la categoria es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    @NotBlank(message = "El slug es obligatorio")
    @Size(max = 120, message = "El slug no puede superar los 120 caracteres")
    private String slug;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String description;

    private Boolean active;

    private UUID parentId;
}
