package com.ecommerce.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class BrandRequestDtoTest {

    private final Validator validator = Validation
            .buildDefaultValidatorFactory()
            .getValidator();

    @Test
    void validDto_shouldHaveNoViolations() {
        BrandRequestDto dto = validDto();

        Set<ConstraintViolation<BrandRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void name_whenBlank_shouldBeInvalid() {
        BrandRequestDto dto = validDto();
        dto.setName("");

        Set<ConstraintViolation<BrandRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void name_whenLongerThan100_shouldBeInvalid() {
        BrandRequestDto dto = validDto();
        dto.setName("A".repeat(101));

        Set<ConstraintViolation<BrandRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void nif_whenBlank_shouldBeInvalid() {
        BrandRequestDto dto = validDto();
        dto.setNif("");

        Set<ConstraintViolation<BrandRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("nif"));
    }

    @Test
    void nif_whenLessThan9Characters_shouldBeInvalid() {
        BrandRequestDto dto = validDto();
        dto.setNif("B1234567");

        Set<ConstraintViolation<BrandRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("nif"));
    }

    @Test
    void nif_whenMoreThan9Characters_shouldBeInvalid() {
        BrandRequestDto dto = validDto();
        dto.setNif("B123456789");

        Set<ConstraintViolation<BrandRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("nif"));
    }

    @Test
    void country_whenLongerThan100_shouldBeInvalid() {
        BrandRequestDto dto = validDto();
        dto.setCountry("A".repeat(101));

        Set<ConstraintViolation<BrandRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("country"));
    }

    @Test
    void website_whenLongerThan255_shouldBeInvalid() {
        BrandRequestDto dto = validDto();
        dto.setWebsite("A".repeat(256));

        Set<ConstraintViolation<BrandRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("website"));
    }

    @Test
    void logo_whenLongerThan255_shouldBeInvalid() {
        BrandRequestDto dto = validDto();
        dto.setLogo("A".repeat(256));

        Set<ConstraintViolation<BrandRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("logo"));
    }

    private BrandRequestDto validDto() {
        return BrandRequestDto.builder()
                .name("Nike")
                .nif("B12345678")
                .country("USA")
                .website("https://nike.com")
                .logo("https://nike.com/logo.png")
                .active(true)
                .build();
    }
}