package com.ramazantrue.domain;

import java.time.Instant;

public record User(
        long id,
        String phone,
        String name,
        Instant createdAt,
        String role
) {
}
