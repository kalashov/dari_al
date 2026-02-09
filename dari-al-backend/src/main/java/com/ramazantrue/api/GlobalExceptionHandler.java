package com.ramazantrue.api;

import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Глобальный обработчик исключений.
 * Ни одна необработанная ошибка не уходит клиенту в виде stack trace.
 * Чувствительные данные не логируются и не отдаются в ответе.
 */
public final class GlobalExceptionHandler {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class.getName());

    private static final String INTERNAL_MESSAGE = "Внутренняя ошибка сервера";

    public static void register(io.javalin.Javalin app) {
        app.exception(ValidationException.class, (e, ctx) -> handleValidation(e, ctx));
        app.exception(IllegalArgumentException.class, (e, ctx) -> handleBadRequest(e, ctx));
        app.exception(SQLException.class, (e, ctx) -> handleSql(e, ctx));
        app.exception(Exception.class, (e, ctx) -> handleGeneric(e, ctx));
    }

    private static void handleValidation(ValidationException e, Context ctx) {
        String code = e.getCode() != null ? e.getCode() : "VALIDATION_ERROR";
        ApiError error = ApiError.of(code, e.getMessage());
        respondError(ctx, 400, error);
        LOG.log(Level.FINE, "Validation error: {0}", e.getMessage());
    }

    private static void handleBadRequest(IllegalArgumentException e, Context ctx) {
        ApiError error = ApiError.of("BAD_REQUEST", e.getMessage());
        respondError(ctx, 400, error);
        LOG.log(Level.FINE, "Bad request: {0}", e.getMessage());
    }

    private static void handleSql(SQLException e, Context ctx) {
        LOG.log(Level.SEVERE, "SQL error (no sensitive data in log)", e);
        ApiError error = ApiError.of("INTERNAL_ERROR", INTERNAL_MESSAGE);
        respondError(ctx, 500, error);
    }

    private static void handleGeneric(Exception e, Context ctx) {
        LOG.log(Level.SEVERE, "Unhandled exception", e);
        ApiError error = ApiError.of("INTERNAL_ERROR", INTERNAL_MESSAGE);
        respondError(ctx, 500, error);
    }

    private static void respondError(Context ctx, int statusCode, ApiError error) {
        ctx.status(statusCode);
        ctx.json(ApiResponse.fail(error));
    }
}
