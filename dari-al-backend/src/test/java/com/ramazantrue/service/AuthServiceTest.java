package com.ramazantrue.service;

import com.ramazantrue.domain.SmsCode;
import com.ramazantrue.domain.User;
import com.ramazantrue.service.otp.OtpGenerator;
import com.ramazantrue.service.otp.OtpHasher;
import com.ramazantrue.service.sms.SmsSender;
import com.ramazantrue.storage.SmsCodeRepository;
import com.ramazantrue.storage.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // Простая заглушка DataSource для тестов
    private static class TestDataSource implements DataSource {
        private final Connection connection;

        TestDataSource(Connection connection) {
            this.connection = connection;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return connection;
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return connection;
        }

        // Остальные методы интерфейса - заглушки
        @Override
        public java.io.PrintWriter getLogWriter() { return null; }
        @Override
        public void setLogWriter(java.io.PrintWriter out) {}
        @Override
        public void setLoginTimeout(int seconds) {}
        @Override
        public int getLoginTimeout() { return 0; }
        @Override
        public java.util.logging.Logger getParentLogger() { return null; }
        @Override
        public <T> T unwrap(Class<T> iface) { return null; }
        @Override
        public boolean isWrapperFor(Class<?> iface) { return false; }
    }

    private DataSource dataSource;

    @Mock
    private SmsCodeRepository smsRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private OtpGenerator otpGenerator;

    @Mock
    private OtpHasher otpHasher;

    @Mock
    private SmsSender smsSender;

    @Mock
    private Connection connection;

    private AuthService authService;
    private static final int TTL_SECONDS = 300;
    private static final int ATTEMPTS = 5;

    @BeforeEach
    void setUp() throws SQLException {
        // Используем простую заглушку вместо мока DataSource
        dataSource = new TestDataSource(connection);

        authService = new AuthService(
                dataSource,
                smsRepo,
                userRepo,
                otpGenerator,
                otpHasher,
                smsSender,
                TTL_SECONDS,
                ATTEMPTS
        );
    }

    @Test
    void sendCode_shouldGenerateCodeAndSaveToDatabase() throws SQLException {
        // Arrange - подготавливаем данные
        String phone = "+79991234567";
        String code = "1234";
        String hash = "test_hash_12345";
        long smsCodeId = 1L;

        // Настраиваем моки - что они должны вернуть
        when(otpGenerator.getCode()).thenReturn(code);
        when(otpHasher.hash(phone, code)).thenReturn(hash);
        when(smsRepo.create(any(Connection.class), eq(phone), eq(hash), any(Instant.class), eq(ATTEMPTS)))
                .thenReturn(smsCodeId);

        // Act - вызываем метод, который тестируем
        authService.sendCode(phone);

        // Assert - проверяем, что все методы были вызваны правильно
        verify(otpGenerator).getCode();
        verify(otpHasher).hash(phone, code);
        verify(smsRepo).create(any(Connection.class), eq(phone), eq(hash), any(Instant.class), eq(ATTEMPTS));
        verify(smsSender).send(phone, code);
        verify(connection).close();
    }

    @Test
    void sendCode_whenDatabaseError_shouldNotSendSms() throws SQLException {
        // Arrange - подготавливаем ошибку БД
        String phone = "+79991234567";
        String code = "1234";
        String hash = "test_hash";

        when(otpGenerator.getCode()).thenReturn(code);
        when(otpHasher.hash(phone, code)).thenReturn(hash);
        when(smsRepo.create(any(Connection.class), anyString(), anyString(), any(Instant.class), anyInt()))
                .thenThrow(new SQLException("Database error"));

        // Act & Assert - проверяем, что выбрасывается исключение и SMS не отправляется
        assertThrows(RuntimeException.class, () -> authService.sendCode(phone));
        verify(smsSender, never()).send(anyString(), anyString());
    }

    @Test
    void verify_whenCodeNotFound_shouldReturnEmpty() throws SQLException {
        // Arrange - код не найден в БД
        String phone = "+79991234567";
        String code = "1234";

        when(smsRepo.findLatestValid(any(Connection.class), eq(phone), any(Instant.class)))
                .thenReturn(Optional.empty());

        // Act
        Optional<User> result = authService.verify(phone, code);

        // Assert - должен вернуть пустой Optional
        assertTrue(result.isEmpty());
        verify(connection).setAutoCommit(false);
        verify(otpHasher).hash(phone, code);
        verify(smsRepo, never()).markUsed(any(Connection.class), anyLong(), any(Instant.class));
        verify(smsRepo, never()).decreaseAttempts(any(Connection.class), anyLong());
        verify(userRepo, never()).findByPhone(any(Connection.class), anyString());
        verify(connection).commit();
        verify(connection).close();
    }

    @Test
    void verify_whenCodeHashDoesNotMatch_shouldDecreaseAttempts() throws SQLException {
        // Arrange - код найден, но хеш не совпадает
        String phone = "+79991234567";
        String code = "1234";
        String wrongHash = "wrong_hash";
        String correctHash = "correct_hash";
        long smsCodeId = 1L;

        SmsCode smsCode = new SmsCode(
                smsCodeId,
                phone,
                correctHash,
                Instant.now().plusSeconds(300),
                3,
                null
        );

        when(smsRepo.findLatestValid(any(Connection.class), eq(phone), any(Instant.class)))
                .thenReturn(Optional.of(smsCode));
        when(otpHasher.hash(phone, code)).thenReturn(wrongHash);

        // Act
        Optional<User> result = authService.verify(phone, code);

        // Assert - должен уменьшить попытки и вернуть пустой Optional
        assertTrue(result.isEmpty());
        verify(connection).setAutoCommit(false);
        verify(otpHasher).hash(phone, code);
        verify(smsRepo).decreaseAttempts(any(Connection.class), eq(smsCodeId));
        verify(smsRepo, never()).markUsed(any(Connection.class), anyLong(), any(Instant.class));
        verify(userRepo, never()).findByPhone(any(Connection.class), anyString());
        verify(connection).commit();
        verify(connection).close();
    }

    @Test
    void verify_whenCodeMatchesAndUserExists_shouldReturnExistingUser() throws SQLException {
        // Arrange - код верный, пользователь существует
        String phone = "+79991234567";
        String code = "1234";
        String hash = "correct_hash";
        long smsCodeId = 1L;
        long userId = 100L;

        SmsCode smsCode = new SmsCode(
                smsCodeId,
                phone,
                hash,
                Instant.now().plusSeconds(300),
                3,
                null
        );

        User existingUser = new User(userId, phone, "Иван", Instant.now(), "USER");

        when(smsRepo.findLatestValid(any(Connection.class), eq(phone), any(Instant.class)))
                .thenReturn(Optional.of(smsCode));
        when(otpHasher.hash(phone, code)).thenReturn(hash);
        when(smsRepo.markUsed(any(Connection.class), eq(smsCodeId), any(Instant.class)))
                .thenReturn(true);
        when(userRepo.findByPhone(any(Connection.class), eq(phone)))
                .thenReturn(Optional.of(existingUser));

        // Act
        Optional<User> result = authService.verify(phone, code);

        // Assert - должен вернуть существующего пользователя
        assertTrue(result.isPresent());
        assertEquals(existingUser, result.get());
        verify(connection).setAutoCommit(false);
        verify(otpHasher).hash(phone, code);
        verify(smsRepo).markUsed(any(Connection.class), eq(smsCodeId), any(Instant.class));
        verify(userRepo).findByPhone(any(Connection.class), eq(phone));
        verify(userRepo, never()).create(any(Connection.class), anyString(), anyString());
        verify(connection).commit();
        verify(connection).close();
    }

    @Test
    void verify_whenCodeMatchesAndUserDoesNotExist_shouldCreateNewUser() throws SQLException {
        // Arrange - код верный, но пользователя нет
        String phone = "+79991234567";
        String code = "1234";
        String hash = "correct_hash";
        long smsCodeId = 1L;
        long newUserId = 200L;

        SmsCode smsCode = new SmsCode(
                smsCodeId,
                phone,
                hash,
                Instant.now().plusSeconds(300),
                3,
                null
        );

        User newUser = new User(newUserId, phone, null, Instant.now(), null);

        when(smsRepo.findLatestValid(any(Connection.class), eq(phone), any(Instant.class)))
                .thenReturn(Optional.of(smsCode));
        when(otpHasher.hash(phone, code)).thenReturn(hash);
        when(smsRepo.markUsed(any(Connection.class), eq(smsCodeId), any(Instant.class)))
                .thenReturn(true);
        when(userRepo.findByPhone(any(Connection.class), eq(phone)))
                .thenReturn(Optional.empty());
        when(userRepo.create(any(Connection.class), eq(phone), isNull()))
                .thenReturn(newUser);

        // Act
        Optional<User> result = authService.verify(phone, code);

        // Assert - должен создать нового пользователя и вернуть его
        assertTrue(result.isPresent());
        assertEquals(newUser, result.get());
        verify(connection).setAutoCommit(false);
        verify(otpHasher).hash(phone, code);
        verify(smsRepo).markUsed(any(Connection.class), eq(smsCodeId), any(Instant.class));
        verify(userRepo).findByPhone(any(Connection.class), eq(phone));
        verify(userRepo).create(any(Connection.class), eq(phone), isNull());
        verify(connection).commit();
        verify(connection).close();
    }

    @Test
    void verify_whenSQLExceptionOccurs_shouldThrowRuntimeException() throws SQLException {
        // Arrange - ошибка БД при поиске кода
        String phone = "+79991234567";
        String code = "1234";

        when(smsRepo.findLatestValid(any(Connection.class), eq(phone), any(Instant.class)))
                .thenThrow(new SQLException("Database error"));

        // Act & Assert - должно выбросить RuntimeException
        assertThrows(RuntimeException.class, () -> authService.verify(phone, code));
        verify(connection).setAutoCommit(false);
        verify(otpHasher).hash(phone, code);
        verify(connection, never()).commit();
        verify(connection).close(); // Connection должен закрыться даже при ошибке (try-with-resources)
    }
}
