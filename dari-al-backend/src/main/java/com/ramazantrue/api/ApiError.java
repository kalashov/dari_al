package com.ramazantrue.api;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Единый формат ошибки API.
 * В ответе клиенту — только безопасные сообщения, без stack trace и чувствительных данных.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
    String code,
    String message,
    Object details
) {
    public static ApiError of(String code, String message) {
        return new ApiError(code, message, null);
    }

    public static ApiError of(String code, String message, Object details) {
        return new ApiError(code, message, details);
    }
}
