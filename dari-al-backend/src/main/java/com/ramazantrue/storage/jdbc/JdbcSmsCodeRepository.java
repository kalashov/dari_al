package com.ramazantrue.storage.jdbc;

import com.ramazantrue.domain.SmsCode;
import com.ramazantrue.storage.SmsCodeRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.Optional;

public class JdbcSmsCodeRepository implements SmsCodeRepository {
    private final DataSource ds;

    public JdbcSmsCodeRepository(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public long create(Connection c, String phone, String codeHash, Instant expiresAt, int attemptsLeft) throws SQLException {
        final String sql = "INSERT INTO sms_codes(phone, code_hash, expires_at, attempts_left) " +
                        "VALUES(?, ?, ?, ?) " +
                        "RETURNING id ";

        try(PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setString(2, codeHash);
            ps.setTimestamp(3, Timestamp.from(expiresAt));
            ps.setInt(4, attemptsLeft);


            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("sms_codes insert: RETURNING id returned no rows");
                }
                return rs.getLong(1);
            }
        }
    }

    @Override
    public Optional<SmsCode> findLatestValid(Connection c, String phone, Instant now) throws SQLException {
        final String sql =
                "SELECT id, phone, code_hash, expires_at, attempts_left, used_at " +
                        "FROM sms_codes " +
                        "WHERE used_at IS NULL " +
                        "  AND phone = ? " +
                        "  AND expires_at > ? " +
                        "  AND attempts_left > 0 " +
                        "ORDER BY created_at DESC " +
                        "LIMIT 1";

        try(PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setTimestamp(2, Timestamp.from(now));


            try (ResultSet rs = ps.executeQuery()) {
                if(!rs.next()) return Optional.empty();

                long id = rs.getLong("id");
                String ph = rs.getString("phone");
                String hash = rs.getString("code_hash");
                Timestamp exp = rs.getTimestamp("expires_at");
                int atms = rs.getInt("attempts_left");
                Timestamp used = rs.getTimestamp("used_at");
                Instant usedAt = (used == null) ? null : used.toInstant();

                return Optional.of(new SmsCode(id, ph, hash, exp.toInstant(), atms, usedAt));
            }
        }
    }

    @Override
    public boolean markUsed(Connection c, long id, Instant usedAt) throws SQLException {
        final String sql = "UPDATE sms_codes " +
                "SET used_at = ? " +
                "WHERE id = ? AND used_at IS NULL";

        try(PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.from(usedAt));
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public void decreaseAttempts(Connection c, long id) {
        final String sql = "UPDATE sms_codes " +
                "SET attempts_left = attempts_left - 1 " +
                "WHERE id = ?";

        try(PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
