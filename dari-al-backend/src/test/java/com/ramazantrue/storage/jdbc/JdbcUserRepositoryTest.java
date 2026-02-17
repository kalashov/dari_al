package com.ramazantrue.storage.jdbc;

import com.ramazantrue.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcUserRepositoryTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private JdbcUserRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        repository = new JdbcUserRepository();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void findByPhone_whenUserExists_shouldReturnUser() throws SQLException {
        // Arrange - пользователь найден
        String phone = "+79991234567";
        long userId = 100L;
        String name = "Иван";
        Instant createdAt = Instant.now();
        String role = "USER";

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(userId);
        when(resultSet.getString("phone")).thenReturn(phone);
        when(resultSet.getString("name")).thenReturn(name);
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.from(createdAt));
        when(resultSet.getString("role")).thenReturn(role);

        // Act
        var result = repository.findByPhone(connection, phone);

        // Assert
        assertTrue(result.isPresent());
        User user = result.get();
        assertEquals(userId, user.id());
        assertEquals(phone, user.phone());
        assertEquals(name, user.name());
        assertEquals(role, user.role());
        assertNotNull(user.createdAt());

        verify(preparedStatement).setString(1, phone);
        verify(preparedStatement).executeQuery();
        verify(resultSet).close();
        verify(preparedStatement).close();
    }

    @Test
    void findByPhone_whenUserNotFound_shouldReturnEmpty() throws SQLException {
        // Arrange - пользователь не найден
        String phone = "+79991234567";

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act
        var result = repository.findByPhone(connection, phone);

        // Assert
        assertTrue(result.isEmpty());
        verify(preparedStatement).setString(1, phone);
        verify(preparedStatement).executeQuery();
        verify(resultSet).close();
        verify(preparedStatement).close();
    }

    @Test
    void create_whenSuccessful_shouldReturnCreatedUser() throws SQLException {
        // Arrange - успешное создание пользователя
        String phone = "+79991234567";
        String name = "Петр";
        long userId = 200L;
        Instant createdAt = Instant.now();
        String role = null;

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(userId);
        when(resultSet.getString("phone")).thenReturn(phone);
        when(resultSet.getString("name")).thenReturn(name);
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.from(createdAt));
        when(resultSet.getString("role")).thenReturn(role);

        // Act
        User result = repository.create(connection, phone, name);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(phone, result.phone());
        assertEquals(name, result.name());
        assertEquals(role, result.role());
        assertNotNull(result.createdAt());

        verify(preparedStatement).setString(1, phone);
        verify(preparedStatement).setString(2, name);
        verify(preparedStatement).executeQuery();
        verify(resultSet).close();
        verify(preparedStatement).close();
    }

    @Test
    void create_whenReturningReturnsNoRows_shouldThrowSQLException() throws SQLException {
        // Arrange - RETURNING не вернул строки
        String phone = "+79991234567";
        String name = "Петр";

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act & Assert
        SQLException exception = assertThrows(SQLException.class, () -> {
            repository.create(connection, phone, name);
        });

        assertTrue(exception.getMessage().contains("RETURNING returned no rows"));
        verify(preparedStatement).setString(1, phone);
        verify(preparedStatement).setString(2, name);
        verify(preparedStatement).executeQuery();
        verify(resultSet).close();
        verify(preparedStatement).close();
    }

    @Test
    void create_whenNameIsNull_shouldHandleNull() throws SQLException {
        // Arrange - создание пользователя без имени
        String phone = "+79991234567";
        String name = null;
        long userId = 300L;
        Instant createdAt = Instant.now();

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(userId);
        when(resultSet.getString("phone")).thenReturn(phone);
        when(resultSet.getString("name")).thenReturn(null);
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.from(createdAt));
        when(resultSet.getString("role")).thenReturn(null);

        // Act
        User result = repository.create(connection, phone, name);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(phone, result.phone());
        assertNull(result.name());
        assertNull(result.role());

        verify(preparedStatement).setString(1, phone);
        verify(preparedStatement).setString(2, name);
    }
}
