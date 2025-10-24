package br.com.bars_register.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleIllegalArgument_DeveRetornar400() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Teste de erro");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Teste de erro", response.getBody().get("error"));
    }

    @Test
    void handleGeneric_DeveRetornar500() {
        // Arrange
        RuntimeException ex = new RuntimeException("Erro genérico");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno", response.getBody().get("error"));
        assertEquals("Erro genérico", response.getBody().get("message"));
    }
}
