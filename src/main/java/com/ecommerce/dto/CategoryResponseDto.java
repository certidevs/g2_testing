package com.ecommerce.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// Lo que el servidor devuelve cuando se consulta o creas una categoria.
public class CategoryResponseDto
{
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private Boolean active;

    private UUID parentId;
    private String parentName;

    private List<CategoryResponseDto> children;
}
