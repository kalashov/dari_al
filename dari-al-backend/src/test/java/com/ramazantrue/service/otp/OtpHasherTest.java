package com.ramazantrue.service.otp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OtpHasherTest {

    @Test
    void hash_withPhoneAndCode_returnsHmacHash() {
        String secret = "testSecret";
        OtpHasher hasher = new OtpHasher(secret);
        
        String phone = "+79991234567";
        String code = "1234";
        
        String result = hasher.hash(phone, code);
        
        // Проверяем, что результат не пустой и имеет правильную длину (64 символа для hex SHA256)
        assertNotNull(result);
        assertEquals(64, result.length());
        assertTrue(result.matches("[0-9a-f]{64}"), "Результат должен быть hex строкой из 64 символов");
    }

    @Test
    void hash_sameInputs_returnsSameHash() {
        String secret = "secret123";
        OtpHasher hasher = new OtpHasher(secret);
        
        String phone = "+79991234567";
        String code = "5678";
        
        String result1 = hasher.hash(phone, code);
        String result2 = hasher.hash(phone, code);
        
        assertEquals(result1, result2, "Одинаковые входные данные должны давать одинаковый хеш");
    }

    @Test
    void hash_differentPhones_returnsDifferentHash() {
        String secret = "secret";
        OtpHasher hasher = new OtpHasher(secret);
        
        String code = "1234";
        String phone1 = "+79991234567";
        String phone2 = "+79991234568";
        
        String result1 = hasher.hash(phone1, code);
        String result2 = hasher.hash(phone2, code);
        
        assertNotEquals(result1, result2, "Разные телефоны должны давать разные хеши");
    }

    @Test
    void hash_differentCodes_returnsDifferentHash() {
        String secret = "secret";
        OtpHasher hasher = new OtpHasher(secret);
        
        String phone = "+79991234567";
        String code1 = "1234";
        String code2 = "5678";
        
        String result1 = hasher.hash(phone, code1);
        String result2 = hasher.hash(phone, code2);
        
        assertNotEquals(result1, result2, "Разные коды должны давать разные хеши");
    }

    @Test
    void hash_differentSecrets_returnsDifferentHash() {
        OtpHasher hasher1 = new OtpHasher("secret1");
        OtpHasher hasher2 = new OtpHasher("secret2");
        
        String phone = "+79991234567";
        String code = "1234";
        
        String result1 = hasher1.hash(phone, code);
        String result2 = hasher2.hash(phone, code);
        
        assertNotEquals(result1, result2, "Разные секреты должны давать разные хеши");
    }

    @Test
    void hash_usesCorrectFormat_phoneColonCode() {
        // Проверяем, что формат данных правильный: phone + ":" + code
        // Для этого создадим два хешера с одинаковым секретом и проверим,
        // что phone:code дает тот же результат, что и вызов hash(phone, code)
        String secret = "testSecret";
        OtpHasher hasher = new OtpHasher(secret);
        
        String phone = "+79991234567";
        String code = "1234";
        
        String result = hasher.hash(phone, code);
        
        // Проверяем, что результат соответствует HMAC(secret, phone + ":" + code)
        // Используем HmacUtil напрямую для проверки формата
        String expectedData = phone + ":" + code;
        String expectedHash = com.ramazantrue.infra.HmacUtil.hmacSha256Hex(secret, expectedData);
        
        assertEquals(expectedHash, result, "Хеш должен быть вычислен для формата phone:code");
    }
}
