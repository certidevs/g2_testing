package com.ecommerce.controller.api;

import com.ecommerce.dto.CategoryRequestDto;
import com.ecommerce.dto.CategoryResponseDto;
import com.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryApiController
{
    /**
     * Servicio que contiene la lógica de negocio para Category.
     * Inyectado automáticamente por Lombok (@RequiredArgsConstructor).
     */
    private final CategoryService categoryService;

    /**
     * GET /api/categories
     * Recupera la lista completa de categorías (plana).
     * Respuesta: 200 OK con la lista de CategoryResponseDto.
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> findAll()
    {
        return ResponseEntity.ok(categoryService.findAll());
    }

    /**
     * GET /api/categories/tree
     * Recupera las categorías raíz con su estructura en árbol (subcategorías anidadas).
     * Útil para menús, navegación y vistas jerárquicas.
     * Respuesta: 200 OK con la lista de CategoryResponseDto representando el árbol.
     */
    @GetMapping("/tree")
    public ResponseEntity<List<CategoryResponseDto>> findRootCategories()
    {
        return ResponseEntity.ok(categoryService.findRootCategories());
    }

    /**
     * GET /api/categories/{id}
     * Recupera una categoría por su identificador UUID.
     * @param id UUID de la categoría a buscar (vía @PathVariable).
     * Respuesta: 200 OK con CategoryResponseDto si existe; el servicio puede lanzar excepción si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> findById(@PathVariable UUID id)
    {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    /**
     * POST /api/categories
     * Crea una nueva categoría a partir de los datos recibidos en el body.
     * @param dto DTO de petición validado con @Valid.
     * Respuesta: 201 Created con el CategoryResponseDto creado.
     */
    @PostMapping
    public ResponseEntity<CategoryResponseDto> create(@Valid @RequestBody CategoryRequestDto dto)
    {
        CategoryResponseDto createdCategory = categoryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    /**
     * PUT /api/categories/{id}
     * Actualiza la categoría identificada por id con los datos del DTO.
     * @param id UUID de la categoría a actualizar.
     * @param dto DTO de petición validado con @Valid.
     * Respuesta: 200 OK con el CategoryResponseDto actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> update(@PathVariable UUID id, @Valid @RequestBody CategoryRequestDto dto)
    {
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    /**
     * DELETE /api/categories/{id}
     * Elimina la categoría indicada por id.
     * @param id UUID de la categoría a eliminar.
     * Respuesta: 204 No Content si la eliminación fue correcta.
     * Nota: considerar reglas de negocio (p. ej. si tiene hijos o productos asociados).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id)
    {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
