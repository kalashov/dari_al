package com.ramazantrue;

import com.ramazantrue.api.ApiResponse;
import com.ramazantrue.api.GlobalExceptionHandler;
import com.ramazantrue.config.AppConfig;
import com.ramazantrue.config.DataSourceFactory;

import io.javalin.Javalin;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Точка входа. Выполняет миграции БД, поднимает HTTP-сервер (Javalin), регистрирует обработчики ошибок и маршруты.
 */
public class App {

    private static final Logger LOG = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        AppConfig config = AppConfig.load();

        DataSource dataSource = DataSourceFactory.create(config);
        runMigrations(dataSource);

        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.showJavalinBanner = false;
        });

        GlobalExceptionHandler.register(app);

        app.get("/health", ctx -> {
            ctx.json(ApiResponse.ok(Map.of("status", "up")));
        });

        app.get("/", ctx -> {
            ctx.json(ApiResponse.ok(Map.of("message", "Dari Al API")));
        });

        app.events(event -> {
            event.serverStarted(() -> {
                LOG.info("Server started on port " + config.httpPort());
            });
            event.serverStartFailed(() -> {
                LOG.severe("Server failed to start (e.g. port " + config.httpPort() + " already in use). Set HTTP_PORT or stop the other process.");
                System.exit(1);
            });
        });

        LOG.info("Starting server on port " + config.httpPort() + "...");
        app.start(config.httpPort());
    }

    private static void runMigrations(DataSource dataSource) {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:db/migration")
                    .load();
            flyway.migrate();
        } catch (Exception e) {
            LOG.severe("Database migration failed: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
