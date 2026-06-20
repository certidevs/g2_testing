package com.ecommerce.controller.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiExceptionAdviceTest {

    private final ApiExceptionAdvice apiExceptionAdvice = new ApiExceptionAdvice();

    @Test
    void handleInternalServerError_whenDataIntegrityViolationException_shouldReturnConflict() {
        DataIntegrityViolationException exception =
                new DataIntegrityViolationException("Error de integridad");

        ResponseEntity<String> response =
                apiExceptionAdvice.handleInternalServerError(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflicto al guardar.", response.getBody());
    }
}