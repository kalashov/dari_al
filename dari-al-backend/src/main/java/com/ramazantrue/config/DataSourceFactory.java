package com.ramazantrue.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * Создание DataSource из конфигурации приложения.
 * Используется для Flyway и сервисов, работающих с БД.
 */
public final class DataSourceFactory {

    private DataSourceFactory() {
    }

    public static DataSource create(AppConfig config) {
        HikariConfig hikari = new HikariConfig();
        hikari.setJdbcUrl(config.dbUrl());
        hikari.setUsername(config.dbUser());
        hikari.setPassword(config.dbPass());
        hikari.setPoolName("dari-al-pool");
        return new HikariDataSource(hikari);
    }
}
