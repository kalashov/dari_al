package com.ramazantrue.api;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Единый формат успешного ответа API.
 * Контроллеры возвращают только этот формат для успешных ответов.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    boolean success,
    T data,
    ApiError error
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> fail(ApiError error) {
        return new ApiResponse<>(false, null, error);
    }
}
