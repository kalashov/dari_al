package com.ramazantrue.storage.jdbc;

import com.ramazantrue.domain.User;
import com.ramazantrue.storage.UserRepository;

import java.sql.*;
import java.time.Instant;
import java.util.Optional;

public class JdbcUserRepository implements UserRepository {

    @Override
    public Optional<User> findByPhone(Connection c, String phone) throws SQLException {
        final String sql = """
                SELECT id, phone, name, created_at, role
                FROM users
                WHERE phone = ?
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(rs));
            }
        }
    }

    @Override
    public User create(Connection c, String phone, String name) throws SQLException {
        final String sql = """
                INSERT INTO users (phone, name)
                VALUES (?, ?)
                RETURNING id, phone, name, created_at, role
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setString(2, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("users insert: RETURNING returned no rows");
                }
                return mapRow(rs);
            }
        }
    }

    private static User mapRow(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String phone = rs.getString("phone");
        String name = rs.getString("name");
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Instant createdAt = createdAtTs != null ? createdAtTs.toInstant() : null;
        String role = rs.getString("role");

        return new User(id, phone, name, createdAt, role);
    }
}

