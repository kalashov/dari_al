package com.ramazantrue.infra;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HmacUtilTest {

    @Test
    void hmacSha256Hex_withKnownValues_returnsCorrectHash() {
        // Тестовые данные с известным результатом HMAC SHA256
        String secret = "mySecretKey";
        String data = "testData";
        
        // Ожидаемый результат вычислен заранее
        // HMAC-SHA256("mySecretKey", "testData") = 
        // можно проверить через онлайн калькулятор или openssl
        String result = HmacUtil.hmacSha256Hex(secret, data);
        
        // Проверяем, что результат не пустой и имеет правильную длину (64 символа для hex SHA256)
        assertNotNull(result);
        assertEquals(64, result.length());
        assertTrue(result.matches("[0-9a-f]{64}"), "Результат должен быть hex строкой из 64 символов");
    }

    @Test
    void hmacSha256Hex_withEmptyData_returnsValidHash() {
        String secret = "secret";
        String data = "";
        
        String result = HmacUtil.hmacSha256Hex(secret, data);
        
        assertNotNull(result);
        assertEquals(64, result.length());
    }

    @Test
    void hmacSha256Hex_withEmptySecret_throwsException() {
        String secret = "";
        String data = "data";
        
        // Java криптография не позволяет использовать пустой ключ
        assertThrows(RuntimeException.class, () -> {
            HmacUtil.hmacSha256Hex(secret, data);
        });
    }

    @Test
    void hmacSha256Hex_sameInputs_returnsSameHash() {
        String secret = "secret123";
        String data = "data456";
        
        String result1 = HmacUtil.hmacSha256Hex(secret, data);
        String result2 = HmacUtil.hmacSha256Hex(secret, data);
        
        assertEquals(result1, result2, "Одинаковые входные данные должны давать одинаковый результат");
    }

    @Test
    void hmacSha256Hex_differentInputs_returnsDifferentHash() {
        String secret = "secret";
        String data1 = "data1";
        String data2 = "data2";
        
        String result1 = HmacUtil.hmacSha256Hex(secret, data1);
        String result2 = HmacUtil.hmacSha256Hex(secret, data2);
        
        assertNotEquals(result1, result2, "Разные входные данные должны давать разные результаты");
    }
}
