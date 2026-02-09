package com.ramazantrue.api;

/**
 * Исключение валидации входящих данных.
 * Обрабатывается глобальным handler → 400 с понятным сообщением клиенту.
 */
public class ValidationException extends RuntimeException {

    private final String code;

    public ValidationException(String message) {
        super(message);
        this.code = "VALIDATION_ERROR";
    }

    public ValidationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
