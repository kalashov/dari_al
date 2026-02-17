package com.ramazantrue.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SmsCodeTest {

    @Test
    void isUsed_whenUsedAtIsNull_shouldReturnFalse() {
        // Arrange - код не использован
        SmsCode smsCode = new SmsCode(
                1L,
                "+79991234567",
                "hash123",
                Instant.now().plusSeconds(300),
                3,
                null
        );

        // Act & Assert
        assertFalse(smsCode.isUsed());
    }

    @Test
    void isUsed_whenUsedAtIsNotNull_shouldReturnTrue() {
        // Arrange - код использован
        Instant usedAt = Instant.now();
        SmsCode smsCode = new SmsCode(
                1L,
                "+79991234567",
                "hash123",
                Instant.now().plusSeconds(300),
                3,
                usedAt
        );

        // Act & Assert
        assertTrue(smsCode.isUsed());
    }

    @Test
    void isExpired_whenExpiresAtIsBeforeNow_shouldReturnTrue() {
        // Arrange - код истек (expiresAt в прошлом)
        Instant now = Instant.now();
        Instant expiredAt = now.minusSeconds(100);
        
        SmsCode smsCode = new SmsCode(
                1L,
                "+79991234567",
                "hash123",
                expiredAt,
                3,
                null
        );

        // Act & Assert
        assertTrue(smsCode.isExpired(now));
    }

    @Test
    void isExpired_whenExpiresAtIsAfterNow_shouldReturnFalse() {
        // Arrange - код еще действителен (expiresAt в будущем)
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(300);
        
        SmsCode smsCode = new SmsCode(
                1L,
                "+79991234567",
                "hash123",
                expiresAt,
                3,
                null
        );

        // Act & Assert
        assertFalse(smsCode.isExpired(now));
    }

    @Test
    void isExpired_whenExpiresAtEqualsNow_shouldReturnFalse() {
        // Arrange - expiresAt равен now (граничный случай)
        Instant now = Instant.now();
        
        SmsCode smsCode = new SmsCode(
                1L,
                "+79991234567",
                "hash123",
                now,
                3,
                null
        );

        // Act & Assert
        // isBefore возвращает false если даты равны, поэтому isExpired должен вернуть false
        assertFalse(smsCode.isExpired(now));
    }
}
