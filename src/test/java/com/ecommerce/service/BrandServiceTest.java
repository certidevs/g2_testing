package com.ecommerce.service;

import com.ecommerce.dto.BrandRequestDto;
import com.ecommerce.dto.BrandResponseDto;
import com.ecommerce.model.Brand;
import com.ecommerce.repository.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @Test
    void findAll_shouldReturnAllBrandsAsDto() {
        Brand nike = Brand.builder()
                .id(UUID.randomUUID())
                .name("Nike")
                .nif("B12345678")
                .country("USA")
                .website("https://nike.com")
                .logo("https://nike.com/logo.png")
                .active(true)
                .build();

        Brand adidas = Brand.builder()
                .id(UUID.randomUUID())
                .name("Adidas")
                .nif("B87654321")
                .country("Germany")
                .website("https://adidas.com")
                .logo("https://adidas.com/logo.png")
                .active(true)
                .build();

        when(brandRepository.findAll()).thenReturn(List.of(nike, adidas));

        List<BrandResponseDto> result = brandService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Nike");
        assertThat(result.get(1).getName()).isEqualTo("Adidas");

        verify(brandRepository).findAll();
    }

    @Test
    void findById_whenBrandExists_shouldReturnDto() {
        UUID id = UUID.randomUUID();

        Brand brand = Brand.builder()
                .id(id)
                .name("Nike")
                .nif("B12345678")
                .country("USA")
                .website("https://nike.com")
                .logo("https://nike.com/logo.png")
                .active(true)
                .build();

        when(brandRepository.findById(id)).thenReturn(Optional.of(brand));

        BrandResponseDto result = brandService.findById(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Nike");
        assertThat(result.getNif()).isEqualTo("B12345678");
        assertThat(result.getActive()).isTrue();

        verify(brandRepository).findById(id);
    }

    @Test
    void findById_whenBrandDoesNotExist_shouldThrowException() {
        UUID id = UUID.randomUUID();

        when(brandRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> brandService.findById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Marca no encontrada");

        verify(brandRepository).findById(id);
    }

    @Test
    void create_whenNameAlreadyExists_shouldThrowException() {
        BrandRequestDto dto = BrandRequestDto.builder()
                .name("Nike")
                .nif("B12345678")
                .country("USA")
                .active(true)
                .build();

        when(brandRepository.existsByName("Nike")).thenReturn(true);

        assertThatThrownBy(() -> brandService.create(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe una marca con ese nombre");

        verify(brandRepository).existsByName("Nike");
        verify(brandRepository, never()).existsByNif(anyString());
        verify(brandRepository, never()).save(any());
    }

    @Test
    void create_whenNifAlreadyExists_shouldThrowException() {
        BrandRequestDto dto = BrandRequestDto.builder()
                .name("Nike")
                .nif("B12345678")
                .country("USA")
                .active(true)
                .build();

        when(brandRepository.existsByName("Nike")).thenReturn(false);
        when(brandRepository.existsByNif("B12345678")).thenReturn(true);

        assertThatThrownBy(() -> brandService.create(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe una marca con ese NIF");

        verify(brandRepository).existsByName("Nike");
        verify(brandRepository).existsByNif("B12345678");
        verify(brandRepository, never()).save(any());
    }

    @Test
    void create_whenDataIsValid_shouldSaveAndReturnDto() {
        UUID generatedId = UUID.randomUUID();

        BrandRequestDto dto = BrandRequestDto.builder()
                .name("Nike")
                .nif("B12345678")
                .country("USA")
                .website("https://nike.com")
                .logo("https://nike.com/logo.png")
                .active(true)
                .build();

        when(brandRepository.existsByName("Nike")).thenReturn(false);
        when(brandRepository.existsByNif("B12345678")).thenReturn(false);

        when(brandRepository.save(any(Brand.class))).thenAnswer(invocation -> {
            Brand brand = invocation.getArgument(0);
            brand.setId(generatedId);
            return brand;
        });

        BrandResponseDto result = brandService.create(dto);

        assertThat(result.getId()).isEqualTo(generatedId);
        assertThat(result.getName()).isEqualTo("Nike");
        assertThat(result.getNif()).isEqualTo("B12345678");
        assertThat(result.getActive()).isTrue();

        ArgumentCaptor<Brand> brandCaptor = ArgumentCaptor.forClass(Brand.class);
        verify(brandRepository).save(brandCaptor.capture());

        Brand savedBrand = brandCaptor.getValue();

        assertThat(savedBrand.getName()).isEqualTo("Nike");
        assertThat(savedBrand.getNif()).isEqualTo("B12345678");
        assertThat(savedBrand.getCountry()).isEqualTo("USA");
        assertThat(savedBrand.getWebsite()).isEqualTo("https://nike.com");
        assertThat(savedBrand.getLogo()).isEqualTo("https://nike.com/logo.png");
        assertThat(savedBrand.getActive()).isTrue();
    }

    @Test
    void create_whenActiveIsNull_shouldSaveBrandAsActive() {
        UUID generatedId = UUID.randomUUID();

        BrandRequestDto dto = BrandRequestDto.builder()
                .name("Nike")
                .nif("B12345678")
                .country("USA")
                .active(null)
                .build();

        when(brandRepository.existsByName("Nike")).thenReturn(false);
        when(brandRepository.existsByNif("B12345678")).thenReturn(false);

        when(brandRepository.save(any(Brand.class))).thenAnswer(invocation -> {
            Brand brand = invocation.getArgument(0);
            brand.setId(generatedId);
            return brand;
        });

        BrandResponseDto result = brandService.create(dto);

        assertThat(result.getActive()).isTrue();

        ArgumentCaptor<Brand> brandCaptor = ArgumentCaptor.forClass(Brand.class);
        verify(brandRepository).save(brandCaptor.capture());

        assertThat(brandCaptor.getValue().getActive()).isTrue();
    }

    @Test
    void update_whenBrandDoesNotExist_shouldThrowException() {
        UUID id = UUID.randomUUID();

        BrandRequestDto dto = BrandRequestDto.builder()
                .name("Nike")
                .nif("B12345678")
                .active(true)
                .build();

        when(brandRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> brandService.update(id, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Marca no encontrada");

        verify(brandRepository).findById(id);
        verify(brandRepository, never()).save(any());
    }

    @Test
    void update_whenAnotherBrandHasSameName_shouldThrowException() {
        UUID id = UUID.randomUUID();

        Brand existingBrand = Brand.builder()
                .id(id)
                .name("Old Name")
                .nif("B11111111")
                .active(true)
                .build();

        BrandRequestDto dto = BrandRequestDto.builder()
                .name("Nike")
                .nif("B12345678")
                .active(true)
                .build();

        when(brandRepository.findById(id)).thenReturn(Optional.of(existingBrand));
        when(brandRepository.existsByNameAndIdNot("Nike", id)).thenReturn(true);

        assertThatThrownBy(() -> brandService.update(id, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe otra marca con ese nombre");

        verify(brandRepository).findById(id);
        verify(brandRepository).existsByNameAndIdNot("Nike", id);
        verify(brandRepository, never()).save(any());
    }

    @Test
    void update_whenAnotherBrandHasSameNif_shouldThrowException() {
        UUID id = UUID.randomUUID();

        Brand existingBrand = Brand.builder()
                .id(id)
                .name("Old Name")
                .nif("B11111111")
                .active(true)
                .build();

        BrandRequestDto dto = BrandRequestDto.builder()
                .name("Nike")
                .nif("B12345678")
                .active(true)
                .build();

        when(brandRepository.findById(id)).thenReturn(Optional.of(existingBrand));
        when(brandRepository.existsByNameAndIdNot("Nike", id)).thenReturn(false);
        when(brandRepository.existsByNifAndIdNot("B12345678", id)).thenReturn(true);

        assertThatThrownBy(() -> brandService.update(id, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe otra marca con ese NIF");

        verify(brandRepository).findById(id);
        verify(brandRepository).existsByNameAndIdNot("Nike", id);
        verify(brandRepository).existsByNifAndIdNot("B12345678", id);
        verify(brandRepository, never()).save(any());
    }

    @Test
    void update_whenDataIsValid_shouldUpdateAndReturnDto() {
        UUID id = UUID.randomUUID();

        Brand existingBrand = Brand.builder()
                .id(id)
                .name("Old Name")
                .nif("B11111111")
                .country("Old Country")
                .website("https://old.com")
                .logo("https://old.com/logo.png")
                .active(true)
                .build();

        BrandRequestDto dto = BrandRequestDto.builder()
                .name("Nike")
                .nif("B12345678")
                .country("USA")
                .website("https://nike.com")
                .logo("https://nike.com/logo.png")
                .active(false)
                .build();

        when(brandRepository.findById(id)).thenReturn(Optional.of(existingBrand));
        when(brandRepository.existsByNameAndIdNot("Nike", id)).thenReturn(false);
        when(brandRepository.existsByNifAndIdNot("B12345678", id)).thenReturn(false);
        when(brandRepository.save(existingBrand)).thenReturn(existingBrand);

        BrandResponseDto result = brandService.update(id, dto);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Nike");
        assertThat(result.getNif()).isEqualTo("B12345678");
        assertThat(result.getCountry()).isEqualTo("USA");
        assertThat(result.getWebsite()).isEqualTo("https://nike.com");
        assertThat(result.getLogo()).isEqualTo("https://nike.com/logo.png");
        assertThat(result.getActive()).isFalse();

        verify(brandRepository).save(existingBrand);
    }

    @Test
    void update_whenActiveIsNull_shouldKeepPreviousActiveValue() {
        UUID id = UUID.randomUUID();

        Brand existingBrand = Brand.builder()
                .id(id)
                .name("Old Name")
                .nif("B11111111")
                .active(true)
                .build();

        BrandRequestDto dto = BrandRequestDto.builder()
                .name("Nike")
                .nif("B12345678")
                .active(null)
                .build();

        when(brandRepository.findById(id)).thenReturn(Optional.of(existingBrand));
        when(brandRepository.existsByNameAndIdNot("Nike", id)).thenReturn(false);
        when(brandRepository.existsByNifAndIdNot("B12345678", id)).thenReturn(false);
        when(brandRepository.save(existingBrand)).thenReturn(existingBrand);

        BrandResponseDto result = brandService.update(id, dto);

        assertThat(result.getActive()).isTrue();
        verify(brandRepository).save(existingBrand);
    }

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        UUID id = UUID.randomUUID();

        brandService.delete(id);

        verify(brandRepository).deleteById(id);
    }

    @Test
    void toResponseDto_shouldMapEntityToDto() {
        UUID id = UUID.randomUUID();

        Brand brand = Brand.builder()
                .id(id)
                .name("Nike")
                .nif("B12345678")
                .country("USA")
                .website("https://nike.com")
                .logo("https://nike.com/logo.png")
                .active(true)
                .build();

        BrandResponseDto result = brandService.toResponseDto(brand);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Nike");
        assertThat(result.getNif()).isEqualTo("B12345678");
        assertThat(result.getCountry()).isEqualTo("USA");
        assertThat(result.getWebsite()).isEqualTo("https://nike.com");
        assertThat(result.getLogo()).isEqualTo("https://nike.com/logo.png");
        assertThat(result.getActive()).isTrue();
    }
}