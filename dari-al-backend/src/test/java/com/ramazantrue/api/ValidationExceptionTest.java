package com.ramazantrue.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationExceptionTest {

    @Test
    void constructor_withMessage_shouldSetDefaultCode() {
        // Arrange & Act
        ValidationException exception = new ValidationException("Invalid input");

        // Assert
        assertEquals("Invalid input", exception.getMessage());
        assertEquals("VALIDATION_ERROR", exception.getCode());
    }

    @Test
    void constructor_withCodeAndMessage_shouldSetCustomCode() {
        // Arrange & Act
        ValidationException exception = new ValidationException("PHONE_INVALID", "Phone number is invalid");

        // Assert
        assertEquals("Phone number is invalid", exception.getMessage());
        assertEquals("PHONE_INVALID", exception.getCode());
    }

    @Test
    void getCode_shouldReturnCode() {
        // Arrange
        ValidationException exception1 = new ValidationException("Test message");
        ValidationException exception2 = new ValidationException("CUSTOM_CODE", "Test message");

        // Act & Assert
        assertEquals("VALIDATION_ERROR", exception1.getCode());
        assertEquals("CUSTOM_CODE", exception2.getCode());
    }

    @Test
    void getMessage_shouldReturnMessage() {
        // Arrange
        String message = "This is a validation error";
        ValidationException exception = new ValidationException(message);

        // Act & Assert
        assertEquals(message, exception.getMessage());
    }
}
