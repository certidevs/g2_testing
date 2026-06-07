package com.ecommerce.repository;

import com.ecommerce.model.Brand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BrandRepositoryTest {

    @Autowired
    private BrandRepository brandRepository;

    @Test
    void save_shouldPersistBrand() {
        Brand brand = Brand.builder()
                .name("Nike")
                .nif("B12345678")
                .country("USA")
                .website("https://nike.com")
                .logo("https://nike.com/logo.png")
                .active(true)
                .build();

        Brand savedBrand = brandRepository.saveAndFlush(brand);

        assertThat(savedBrand.getId()).isNotNull();
        assertThat(savedBrand.getName()).isEqualTo("Nike");
        assertThat(savedBrand.getNif()).isEqualTo("B12345678");
        assertThat(savedBrand.getCreatedAt()).isNotNull();
        assertThat(savedBrand.getActive()).isTrue();
    }

    @Test
    void save_whenActiveIsNull_shouldSetActiveTrueByPrePersist() {
        Brand brand = Brand.builder()
                .name("Nike")
                .nif("B12345678")
                .country("USA")
                .active(null)
                .build();

        Brand savedBrand = brandRepository.saveAndFlush(brand);

        assertThat(savedBrand.getActive()).isTrue();
        assertThat(savedBrand.getCreatedAt()).isNotNull();
    }

    @Test
    void findByName_whenBrandExists_shouldReturnBrand() {
        Brand brand = Brand.builder()
                .name("Nike")
                .nif("B12345678")
                .active(true)
                .build();

        brandRepository.saveAndFlush(brand);

        Optional<Brand> result = brandRepository.findByName("Nike");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Nike");
    }

    @Test
    void findByName_whenBrandDoesNotExist_shouldReturnEmpty() {
        Optional<Brand> result = brandRepository.findByName("Nike");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByName_whenBrandExists_shouldReturnTrue() {
        Brand brand = Brand.builder()
                .name("Nike")
                .nif("B12345678")
                .active(true)
                .build();

        brandRepository.saveAndFlush(brand);

        boolean exists = brandRepository.existsByName("Nike");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_whenBrandDoesNotExist_shouldReturnFalse() {
        boolean exists = brandRepository.existsByName("Nike");

        assertThat(exists).isFalse();
    }

    @Test
    void findByNif_whenBrandExists_shouldReturnBrand() {
        Brand brand = Brand.builder()
                .name("Nike")
                .nif("B12345678")
                .active(true)
                .build();

        brandRepository.saveAndFlush(brand);

        Optional<Brand> result = brandRepository.findByNif("B12345678");

        assertThat(result).isPresent();
        assertThat(result.get().getNif()).isEqualTo("B12345678");
    }

    @Test
    void existsByNif_whenBrandExists_shouldReturnTrue() {
        Brand brand = Brand.builder()
                .name("Nike")
                .nif("B12345678")
                .active(true)
                .build();

        brandRepository.saveAndFlush(brand);

        boolean exists = brandRepository.existsByNif("B12345678");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByNameAndIdNot_whenAnotherBrandHasSameName_shouldReturnTrue() {
        Brand brand = Brand.builder()
                .name("Nike")
                .nif("B12345678")
                .active(true)
                .build();

        Brand savedBrand = brandRepository.saveAndFlush(brand);

        UUID differentId = UUID.randomUUID();

        boolean exists = brandRepository.existsByNameAndIdNot("Nike", differentId);

        assertThat(exists).isTrue();
        assertThat(savedBrand.getId()).isNotEqualTo(differentId);
    }

    @Test
    void existsByNameAndIdNot_whenSameBrandHasSameName_shouldReturnFalse() {
        Brand brand = Brand.builder()
                .name("Nike")
                .nif("B12345678")
                .active(true)
                .build();

        Brand savedBrand = brandRepository.saveAndFlush(brand);

        boolean exists = brandRepository.existsByNameAndIdNot("Nike", savedBrand.getId());

        assertThat(exists).isFalse();
    }

    @Test
    void existsByNifAndIdNot_whenAnotherBrandHasSameNif_shouldReturnTrue() {
        Brand brand = Brand.builder()
                .name("Nike")
                .nif("B12345678")
                .active(true)
                .build();

        Brand savedBrand = brandRepository.saveAndFlush(brand);

        UUID differentId = UUID.randomUUID();

        boolean exists = brandRepository.existsByNifAndIdNot("B12345678", differentId);

        assertThat(exists).isTrue();
        assertThat(savedBrand.getId()).isNotEqualTo(differentId);
    }

    @Test
    void existsByNifAndIdNot_whenSameBrandHasSameNif_shouldReturnFalse() {
        Brand brand = Brand.builder()
                .name("Nike")
                .nif("B12345678")
                .active(true)
                .build();

        Brand savedBrand = brandRepository.saveAndFlush(brand);

        boolean exists = brandRepository.existsByNifAndIdNot("B12345678", savedBrand.getId());

        assertThat(exists).isFalse();
    }
}