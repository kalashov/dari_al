package com.ramazantrue.storage.jdbc;

import com.ramazantrue.domain.SmsCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcSmsCodeRepositoryTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private JdbcSmsCodeRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        repository = new JdbcSmsCodeRepository(dataSource);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void create_whenSuccessful_shouldReturnId() throws SQLException {
        // Arrange - успешное создание SMS кода
        String phone = "+79991234567";
        String codeHash = "hash123";
        Instant expiresAt = Instant.now().plusSeconds(300);
        int attemptsLeft = 5;
        long smsCodeId = 1L;

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong(1)).thenReturn(smsCodeId);

        // Act
        long result = repository.create(connection, phone, codeHash, expiresAt, attemptsLeft);

        // Assert
        assertEquals(smsCodeId, result);
        verify(preparedStatement).setString(1, phone);
        verify(preparedStatement).setString(2, codeHash);
        verify(preparedStatement).setTimestamp(3, Timestamp.from(expiresAt));
        verify(preparedStatement).setInt(4, attemptsLeft);
        verify(preparedStatement).executeQuery();
        verify(resultSet).close();
        verify(preparedStatement).close();
    }

    @Test
    void create_whenReturningReturnsNoRows_shouldThrowSQLException() throws SQLException {
        // Arrange - RETURNING не вернул строки
        String phone = "+79991234567";
        String codeHash = "hash123";
        Instant expiresAt = Instant.now().plusSeconds(300);
        int attemptsLeft = 5;

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act & Assert
        SQLException exception = assertThrows(SQLException.class, () -> {
            repository.create(connection, phone, codeHash, expiresAt, attemptsLeft);
        });

        assertTrue(exception.getMessage().contains("RETURNING id returned no rows"));
    }

    @Test
    void findLatestValid_whenCodeFound_shouldReturnSmsCode() throws SQLException {
        // Arrange - код найден
        String phone = "+79991234567";
        long smsCodeId = 1L;
        String codeHash = "hash123";
        Instant expiresAt = Instant.now().plusSeconds(300);
        int attemptsLeft = 3;
        Instant now = Instant.now();

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(smsCodeId);
        when(resultSet.getString("phone")).thenReturn(phone);
        when(resultSet.getString("code_hash")).thenReturn(codeHash);
        when(resultSet.getTimestamp("expires_at")).thenReturn(Timestamp.from(expiresAt));
        when(resultSet.getInt("attempts_left")).thenReturn(attemptsLeft);
        when(resultSet.getTimestamp("used_at")).thenReturn(null);

        // Act
        Optional<SmsCode> result = repository.findLatestValid(connection, phone, now);

        // Assert
        assertTrue(result.isPresent());
        SmsCode smsCode = result.get();
        assertEquals(smsCodeId, smsCode.id());
        assertEquals(phone, smsCode.phone());
        assertEquals(codeHash, smsCode.codeHash());
        assertEquals(attemptsLeft, smsCode.attemptsLeft());
        assertNull(smsCode.usedAt());

        verify(preparedStatement).setString(1, phone);
        verify(preparedStatement).setTimestamp(2, Timestamp.from(now));
        verify(preparedStatement).executeQuery();
        verify(resultSet).close();
        verify(preparedStatement).close();
    }

    @Test
    void findLatestValid_whenCodeNotFound_shouldReturnEmpty() throws SQLException {
        // Arrange - код не найден
        String phone = "+79991234567";
        Instant now = Instant.now();

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act
        Optional<SmsCode> result = repository.findLatestValid(connection, phone, now);

        // Assert
        assertTrue(result.isEmpty());
        verify(preparedStatement).setString(1, phone);
        verify(preparedStatement).setTimestamp(2, Timestamp.from(now));
        verify(preparedStatement).executeQuery();
    }

    @Test
    void findLatestValid_whenCodeIsUsed_shouldReturnUsedAt() throws SQLException {
        // Arrange - код использован (used_at не null)
        String phone = "+79991234567";
        long smsCodeId = 1L;
        String codeHash = "hash123";
        Instant expiresAt = Instant.now().plusSeconds(300);
        int attemptsLeft = 3;
        Instant usedAt = Instant.now();
        Instant now = Instant.now();

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(smsCodeId);
        when(resultSet.getString("phone")).thenReturn(phone);
        when(resultSet.getString("code_hash")).thenReturn(codeHash);
        when(resultSet.getTimestamp("expires_at")).thenReturn(Timestamp.from(expiresAt));
        when(resultSet.getInt("attempts_left")).thenReturn(attemptsLeft);
        when(resultSet.getTimestamp("used_at")).thenReturn(Timestamp.from(usedAt));

        // Act
        Optional<SmsCode> result = repository.findLatestValid(connection, phone, now);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(usedAt, result.get().usedAt());
    }

    @Test
    void markUsed_whenSuccessful_shouldReturnTrue() throws SQLException {
        // Arrange - успешная пометка как использованного
        long smsCodeId = 1L;
        Instant usedAt = Instant.now();

        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        boolean result = repository.markUsed(connection, smsCodeId, usedAt);

        // Assert
        assertTrue(result);
        verify(preparedStatement).setTimestamp(1, Timestamp.from(usedAt));
        verify(preparedStatement).setLong(2, smsCodeId);
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).close();
    }

    @Test
    void markUsed_whenNoRowsUpdated_shouldReturnFalse() throws SQLException {
        // Arrange - не обновлено ни одной строки (код уже использован или не найден)
        long smsCodeId = 1L;
        Instant usedAt = Instant.now();

        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Act
        boolean result = repository.markUsed(connection, smsCodeId, usedAt);

        // Assert
        assertFalse(result);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void decreaseAttempts_whenSuccessful_shouldDecreaseAttempts() throws SQLException {
        // Arrange - успешное уменьшение попыток
        long smsCodeId = 1L;

        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        repository.decreaseAttempts(connection, smsCodeId);

        // Assert
        verify(preparedStatement).setLong(1, smsCodeId);
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).close();
    }

    @Test
    void decreaseAttempts_whenSQLException_shouldThrowRuntimeException() throws SQLException {
        // Arrange - ошибка БД при уменьшении попыток
        long smsCodeId = 1L;

        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            repository.decreaseAttempts(connection, smsCodeId);
        });

        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof SQLException);
    }
}
