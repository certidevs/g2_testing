package com.ecommerce.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// Lo que el cliente manda para crear/editar un producto
public class CategoryRequestDto
{
    private String name;
    private String slug;
    private String description;
    private Boolean active;

    private UUID parentId;
}
