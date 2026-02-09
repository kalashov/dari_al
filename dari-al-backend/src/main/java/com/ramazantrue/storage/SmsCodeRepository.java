package com.ramazantrue.storage;

import com.ramazantrue.domain.SmsCode;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;

public interface SmsCodeRepository {
    long create(Connection c, String phone, String codeHash, Instant expiresAt, int attemptsLeft) throws SQLException;

    Optional<SmsCode> findLatestValid(Connection c, String phone, Instant now) throws SQLException;

    boolean markUsed(Connection c, long id, Instant usedAt) throws SQLException;

    void decreaseAttempts(Connection c, long id);
}
