package com.ramazantrue.domain;

import java.time.Instant;

public record SmsCode(
        long id,
        String phone,
        String codeHash,
        Instant expiresAt,
        int attemptsLeft,
        Instant usedAt
) {
    public boolean isUsed() {
        return usedAt != null;
    }
    public boolean isExpired(Instant now) {
        return expiresAt.isBefore(now);
    }
}
