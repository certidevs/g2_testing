package com.ecommerce.service;

import com.ecommerce.dto.BrandRequestDto;
import com.ecommerce.dto.BrandResponseDto;
import com.ecommerce.model.Brand;
import com.ecommerce.repository.BrandRepository;
import jakarta.transaction.Transactional;
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

    /**
     * Crea una nueva marca a partir de un DTO de petición.
     * Realiza validaciones de unicidad sobre nombre y NIF antes de persistir.
     *
     * @param dto BrandRequestDto con los datos para crear la marca.
     * @return BrandResponseDto con la marca creada (incluye id generado).
     * @throws RuntimeException si ya existe una marca con el mismo nombre o NIF.
     */
    @Transactional
    public BrandResponseDto create(BrandRequestDto dto)
    {
        //Validacion: no permite nombres duplicados
        if (brandRepository.existsByName(dto.getName()))
        {
            throw new RuntimeException("Ya existe una marca con ese nombre");
        }

        //Validacion: no permite NIF duplicados
        if (brandRepository.existsByNif(dto.getNif()))
        {
            throw new RuntimeException("Ya existe una marca con ese NIF");
        }

        // Construccion de la entidad a partir del DTO
        Brand brand = Brand.builder()
                .name(dto.getName())
                .nif(dto.getNif())
                .country(dto.getCountry())
                .website(dto.getWebsite())
                .logo(dto.getLogo())
                .active(dto.getActive())
                .build();

        //Persistencia y conversion a DTO de respuesta
        return toResponseDto(brandRepository.save(brand));
    }

    /**
     * Actualiza una marca existente con los datos del DTO.
     * Verifica que la marca exista y que los campos únicos no colisionen con otras marcas.
     *
     * @param id  UUID de la marca a actualizar.
     * @param dto BrandRequestDto con los nuevos valores.
     * @return BrandResponseDto con la marca actualizada.
     * @throws RuntimeException si la marca no existe o si hay conflictos de unicidad.
     */
    @Transactional
    public BrandResponseDto update(UUID id, BrandRequestDto dto)
    {
        //Recuperar la entidad; lanzar excepción si no existe
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand no found"));

        //Validación: nombre único entre otras marcas (excluyendo la actual)
        if (brandRepository.existsByNameAndIdNot(dto.getName(), id))
        {
            throw new RuntimeException("Ya existe otra marca con ese nombre");
        }

        //Validación: NIF único entre otras marcas (excluyendo la actual)
        if (brandRepository.existsByNifAndIdNot(dto.getNif(), id))
        {
            throw new RuntimeException("Ya existe otra marca con ese NIF");
        }

        //Aplicar cambios a la entidad recuperada
        brand.setName(dto.getName());
        brand.setNif(dto.getNif());
        brand.setCountry(dto.getCountry());
        brand.setWebsite(dto.getWebsite());
        brand.setLogo(dto.getLogo());
        brand.setActive(dto.getActive());

        //Guardar y devolver DTO actualizado
        return toResponseDto(brandRepository.save(brand));
    }

    /*Elimina una marca por su id
     * @param id UUID de la marca a eliminar*/
    public void delete(UUID id)
    {
        brandRepository.deleteById(id);
    }

    /**
     * Método auxiliar que transforma una entidad Brand a BrandResponseDto.
     * Centraliza la conversión para evitar duplicación de código.
     * @param brand entidad a convertir.
     * @return DTO de respuesta con los campos relevantes.
     */
    public BrandResponseDto toResponseDto(Brand brand)
    {
        return BrandResponseDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .nif(brand.getNif())
                .country(brand.getCountry())
                .website(brand.getWebsite())
                .logo(brand.getLogo())
                .active(brand.getActive())
                .build();
    }
}
