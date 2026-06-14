package com.ecommerce.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;

class UserRequestDtoTest
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
        UserRequestDto dto = validDto();

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void whenUsernameIsBlank_shouldHaveValidationError()
    {
        UserRequestDto dto = validDto();
        dto.setUsername("");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("username"));
    }

    @Test
    void whenUsernameIsNull_shouldHaveValidationError()
    {
        UserRequestDto dto = validDto();
        dto.setUsername(null);

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("username"));
    }

    @Test
    void whenEmailIsBlank_shouldHaveValidationError()
    {
        UserRequestDto dto = validDto();
        dto.setEmail("");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("email"));
    }

    @Test
    void whenEmailIsNull_shouldHaveValidationError()
    {
        UserRequestDto dto = validDto();
        dto.setEmail(null);

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("email"));
    }

    @Test
    void whenEmailFormatIsInvalid_shouldHaveValidationError()
    {
        UserRequestDto dto = validDto();
        dto.setEmail("correo-invalido");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("email"));
    }

    @Test
    void whenPasswordIsBlank_shouldHaveValidationError()
    {
        UserRequestDto dto = validDto();
        dto.setPassword("");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("password"));
    }

    @Test
    void whenPasswordIsNull_shouldHaveValidationError()
    {
        UserRequestDto dto = validDto();
        dto.setPassword(null);

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("password"));
    }

    @Test
    void whenPasswordHasLessThan8Characters_shouldHaveValidationError()
    {
        UserRequestDto dto = validDto();
        dto.setPassword("Aa1!");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("password"));
    }

    @Test
    void whenPasswordDoesNotHaveUppercase_shouldHaveValidationError()
    {
        UserRequestDto dto = validDto();
        dto.setPassword("password1!");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("password"));
    }

    @Test
    void whenPasswordDoesNotHaveLowercase_shouldHaveValidationError()
    {
        UserRequestDto dto = validDto();
        dto.setPassword("PASSWORD1!");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("password"));
    }

    @Test
    void whenPasswordDoesNotHaveNumber_shouldHaveValidationError()
    {
        UserRequestDto dto = validDto();
        dto.setPassword("Password!");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("password"));
    }

    @Test
    void whenPasswordDoesNotHaveSpecialCharacter_shouldHaveValidationError()
    {
        UserRequestDto dto = validDto();
        dto.setPassword("Password1");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("password"));
    }

    @Test
    void whenPasswordConfirmIsBlank_shouldHaveValidationError()
    {
        UserRequestDto dto = validDto();
        dto.setPasswordConfirm("");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("passwordConfirm"));
    }

    @Test
    void whenPasswordConfirmIsNull_shouldHaveValidationError()
    {
        UserRequestDto dto = validDto();
        dto.setPasswordConfirm(null);

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("passwordConfirm"));
    }

    @Test
    void whenPasswordAndPasswordConfirmAreDifferent_shouldHaveNoDtoValidationError()
    {
        UserRequestDto dto = validDto();
        dto.setPasswordConfirm("Different1!");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    private UserRequestDto validDto()
    {
        return UserRequestDto.builder()
                .username("yepe")
                .email("yepe@example.com")
                .password("Password1!")
                .passwordConfirm("Password1!")
                .build();
    }
}