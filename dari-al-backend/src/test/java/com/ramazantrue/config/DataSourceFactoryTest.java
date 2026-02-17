package com.ramazantrue.config;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

class DataSourceFactoryTest {

    @Test
    void create_shouldReturnDataSource() {
        // Arrange - создаем конфигурацию
        AppConfig config = new AppConfig(
                "jdbc:postgresql://localhost:5433/test_db",
                "test_user",
                "test_pass",
                "test_secret_12345",
                300,
                5,
                8080,
                "http://localhost:5173"
        );

        // Act
        DataSource dataSource = DataSourceFactory.create(config);

        // Assert - проверяем, что DataSource создан
        assertNotNull(dataSource);
        assertTrue(dataSource instanceof com.zaxxer.hikari.HikariDataSource);
    }

    @Test
    void create_shouldConfigureDataSourceCorrectly() {
        // Arrange
        String dbUrl = "jdbc:postgresql://localhost:5433/test_db";
        String dbUser = "test_user";
        String dbPass = "test_pass";

        AppConfig config = new AppConfig(
                dbUrl,
                dbUser,
                dbPass,
                "test_secret_12345",
                300,
                5,
                8080,
                "http://localhost:5173"
        );

        // Act
        DataSource dataSource = DataSourceFactory.create(config);

        // Assert
        assertNotNull(dataSource);
        // Проверяем, что это HikariDataSource
        assertTrue(dataSource instanceof com.zaxxer.hikari.HikariDataSource);
        
        // Проверяем, что можно получить соединение (если БД доступна)
        // Но в unit-тесте мы не проверяем реальное подключение
        // Просто проверяем, что объект создан корректно
    }

    @Test
    void create_withDifferentConfigs_shouldCreateDifferentDataSources() {
        // Arrange - две разные конфигурации
        AppConfig config1 = new AppConfig(
                "jdbc:postgresql://localhost:5433/db1",
                "user1",
                "pass1",
                "secret1_12345",
                300,
                5,
                8080,
                "http://localhost:5173"
        );

        AppConfig config2 = new AppConfig(
                "jdbc:postgresql://localhost:5433/db2",
                "user2",
                "pass2",
                "secret2_12345",
                600,
                10,
                9090,
                "http://localhost:3000"
        );

        // Act
        DataSource dataSource1 = DataSourceFactory.create(config1);
        DataSource dataSource2 = DataSourceFactory.create(config2);

        // Assert
        assertNotNull(dataSource1);
        assertNotNull(dataSource2);
        // Это разные объекты
        assertNotSame(dataSource1, dataSource2);
    }
}
