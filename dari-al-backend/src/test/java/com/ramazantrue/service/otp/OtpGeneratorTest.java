package com.ramazantrue.service.otp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OtpGeneratorTest {

    @Test
    void returnsFixedCode1234() {
        OtpGenerator gen = new OtpGenerator();
        assertEquals("1234", gen.getCode());
    }
}