package com.ecommerce.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryRequestDtoTest
{
    private static Validator validator;

    @BeforeAll
    static void setUpValidator()
    {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenDtoIsValid_shouldHaveNoValidationErrors()
    {
        CategoryRequestDto dto = CategoryRequestDto.builder()
                .name("Portátiles")
                .slug("portatiles")
                .description("Categoría de portátiles")
                .imageUrl("https://example.com/images/portatiles.png")
                .active(true)
                .parentId(UUID.randomUUID())
                .build();

        Set<ConstraintViolation<CategoryRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void whenNameIsBlank_shouldHaveValidationError()
    {
        CategoryRequestDto dto = validDto();
        dto.setName("");

        Set<ConstraintViolation<CategoryRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("name"));
    }

    @Test
    void whenNameIsNull_shouldHaveValidationError()
    {
        CategoryRequestDto dto = validDto();
        dto.setName(null);

        Set<ConstraintViolation<CategoryRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("name"));
    }

    @Test
    void whenNameIsLongerThan100Characters_shouldHaveValidationError()
    {
        CategoryRequestDto dto = validDto();
        dto.setName("a".repeat(101));

        Set<ConstraintViolation<CategoryRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("name"));
    }

    @Test
    void whenSlugIsBlank_shouldHaveValidationError()
    {
        CategoryRequestDto dto = validDto();
        dto.setSlug("");

        Set<ConstraintViolation<CategoryRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("slug"));
    }

    @Test
    void whenSlugIsNull_shouldHaveValidationError()
    {
        CategoryRequestDto dto = validDto();
        dto.setSlug(null);

        Set<ConstraintViolation<CategoryRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("slug"));
    }

    @Test
    void whenSlugIsLongerThan120Characters_shouldHaveValidationError()
    {
        CategoryRequestDto dto = validDto();
        dto.setSlug("a".repeat(121));

        Set<ConstraintViolation<CategoryRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("slug"));
    }

    @Test
    void whenDescriptionIsLongerThan500Characters_shouldHaveValidationError()
    {
        CategoryRequestDto dto = validDto();
        dto.setDescription("a".repeat(501));

        Set<ConstraintViolation<CategoryRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("description"));
    }

    @Test
    void whenImageUrlIsInvalid_shouldHaveValidationError()
    {
        CategoryRequestDto dto = validDto();
        dto.setImageUrl("invalid-url");

        Set<ConstraintViolation<CategoryRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("imageUrl"));
    }

    @Test
    void whenImageUrlIsLongerThan500Characters_shouldHaveValidationError()
    {
        CategoryRequestDto dto = validDto();
        dto.setImageUrl("https://example.com/" + "a".repeat(500));

        Set<ConstraintViolation<CategoryRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("imageUrl"));
    }

    @Test
    void whenOptionalFieldsAreNull_shouldHaveNoValidationErrors()
    {
        CategoryRequestDto dto = CategoryRequestDto.builder()
                .name("Sobremesa")
                .slug("sobremesa")
                .description(null)
                .imageUrl(null)
                .active(null)
                .parentId(null)
                .build();

        Set<ConstraintViolation<CategoryRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    private CategoryRequestDto validDto()
    {
        return CategoryRequestDto.builder()
                .name("Portátiles")
                .slug("portatiles")
                .description("Categoría de portátiles")
                .imageUrl("https://example.com/images/portatiles.png")
                .active(true)
                .parentId(null)
                .build();
    }
}