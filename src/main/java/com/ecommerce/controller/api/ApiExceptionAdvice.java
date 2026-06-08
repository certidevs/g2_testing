package com.ecommerce.controller.api;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.ecommerce.controller.api")
public class ApiExceptionAdvice {

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<String> handleInternalServerError(Exception ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflicto al guardar.");
    }

}
