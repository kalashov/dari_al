package com.ramazantrue.config;

public record AppConfig(
        String dbUrl,
        String dbUser,
        String dbPass,
        String otpSecret,
        int otpTtlSeconds,
        int otpAttempts,
        int httpPort,
        String corsOrigin
) {
    public static AppConfig load() {
        String dbUrl = env("DB_URL", "jdbc:postgresql://localhost:5433/dari_al");
        String dbUser = env("DB_USER", "root");
        String dbPass = env("DB_PASS", "toor");

        String otpSecret = env("OPT_SECRET", "flappybird123");
        int ttl = envInt("OTP_TTL_SECONDS", 300);
        int attempts = envInt("OTP_ATTEMPTS", 5);
        int port = envInt("HTTP_PORT", 8080);
        String corsOrigin = env("CORS_ORIGIN", "http://localhost:5173");

        if(otpSecret.length() < 12) {
            throw new IllegalStateException("OTP secret is too short");
        }

        return new AppConfig(dbUrl, dbUser, dbPass, otpSecret, ttl, attempts, port, corsOrigin);
    }

    private static String env(String key, String def) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? def : value;
    }

    private static int envInt(String key, int def) {
        String value = System.getenv(key);
        if(value == null || value.isBlank()) return def;
        return Integer.parseInt(value.trim());
    }
}