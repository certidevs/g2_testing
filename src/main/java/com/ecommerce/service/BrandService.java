package com.ecommerce.service;

import com.ecommerce.dto.BrandRequestDto;
import com.ecommerce.dto.BrandResponseDto;
import com.ecommerce.model.Brand;
import com.ecommerce.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BrandService
{
    // Repositorio para las operaciones CRUD sobre Brand (inyectado por Spring)
    private final BrandRepository brandRepository;

    /*Recupera todas las marcas y las transforma a DTOs de respuesta
     * @return lista de BrandResponseDto con todas las marcas*/
    public List<BrandResponseDto> findAll()
    {
        return brandRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    /*Busca una marca por su id
     * @param id UUID de la marca a buscar
     * @return BrandResponseDto con los datos de la marca encontrada
     * @throws RuntimeException si no existe la marca*/
    public BrandResponseDto findById(UUID id)
    {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand no found"));
        return toResponseDto(brand);
    }

    /*Crea una nueva marca a partir de un DTO de petición
     * @param dto BrandRequestDto con los datos para crear la marca
     * @return BrandResponseDto con la marca creada (incluye id generado)*/
    public BrandResponseDto create(BrandRequestDto dto)
    {
        Brand brand = Brand.builder()
                .name(dto.getName())
                .country(dto.getCountry())
                .website(dto.getWebsite())
                .active(dto.getActive())
                .build();
        return toResponseDto(brandRepository.save(brand));
    }

    /*Actualiza una marca existente con los datos del DTO
     * @param id UUID de la marca a actualizar
     * @param dto BrandRequestDto con los nuevos valores
     * @return BrandResponseDto con la marca actualizada
     * @throws RuntimeExecption si la marca no existe*/
    public BrandResponseDto update(UUID id, BrandRequestDto dto)
    {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand no found"));

        brand.setName(dto.getName());
        brand.setCountry(dto.getCountry());
        brand.setWebsite(dto.getWebsite());
        brand.setActive(dto.getActive());

        return toResponseDto(brandRepository.save(brand));
    }

    /*Elimina una marca por su id
     * @param id UUID de la marca a eliminar*/
    public void delete(UUID id)
    {
        brandRepository.deleteById(id);
    }

    /*Método auxiliar privado que transforma una entidad Brand a BrandResponseDto
     * Centraliza la conversion para evitar duplicación de código
     * @param brand entidad a convertir
     * @return DTO de respuesta con los campos relevantes*/
    public BrandResponseDto toResponseDto(Brand brand)
    {
        return BrandResponseDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .country(brand.getCountry())
                .website(brand.getWebsite())
                .active(brand.getActive())
                .build();
    }
}
