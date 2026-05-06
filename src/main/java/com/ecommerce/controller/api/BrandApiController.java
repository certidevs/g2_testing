package com.ecommerce.controller.api;

import com.ecommerce.dto.BrandRequestDto;
import com.ecommerce.dto.BrandResponseDto;
import com.ecommerce.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para gestionar marcas (Brand).
 * Expone endpoints CRUD bajo la ruta base /api/brands.
 * Usa inyección por constructor (Lombok @RequiredArgsConstructor) para BrandService.
 */
@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandApiController
{
    /**
     * Servicio que contiene la lógica de negocio para Brand.
     * Se inyecta automáticamente por Lombok (@RequiredArgsConstructor).
     */
    private final BrandService brandService;

    /**
     * GET /api/brands
     * Recupera la lista completa de marcas.
     * Respuesta: 200 OK con la lista de BrandResponseDto.
     */
    @GetMapping
    public ResponseEntity<List<BrandResponseDto>> findAll()
    {
        return ResponseEntity.ok(brandService.findAll());
    }

    /**
     * GET /api/brands/{id}
     * Recupera una marca por su identificador UUID.
     * @param id UUID de la marca a buscar (vía @PathVariable).
     * Respuesta: 200 OK con BrandResponseDto si existe; el servicio puede lanzar excepción si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponseDto> findById(@PathVariable UUID id)
    {
        return ResponseEntity.ok(brandService.findById(id));
    }

    /**
     * POST /api/brands
     * Crea una nueva marca a partir de los datos recibidos en el body.
     * @param dto DTO de petición validado con @Valid.
     * Respuesta: 200 OK con el BrandResponseDto creado (puedes cambiar a 201 Created si prefieres).
     */
    @PostMapping
    public ResponseEntity<BrandResponseDto> create(@Valid @RequestBody BrandRequestDto dto)
    {
        return ResponseEntity.ok(brandService.create(dto));
    }

    /**
     * PUT /api/brands/{id}
     * Actualiza la marca identificada por id con los datos del DTO.
     * @param id UUID de la marca a actualizar.
     * @param dto DTO de petición validado con @Valid.
     * Respuesta: 200 OK con el BrandResponseDto actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BrandResponseDto> update(@PathVariable UUID id, @Valid @RequestBody BrandRequestDto dto)
    {
        return ResponseEntity.ok(brandService.update(id, dto));
    }

    /**
     * DELETE /api/brands/{id}
     * Elimina la marca indicada por id.
     * @param id UUID de la marca a eliminar.
     * Respuesta: 204 No Content si la eliminación fue correcta.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id)
    {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
