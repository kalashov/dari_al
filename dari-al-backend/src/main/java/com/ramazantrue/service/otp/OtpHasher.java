package com.ramazantrue.service.otp;

import com.ramazantrue.infra.HmacUtil;

public class OtpHasher {
    private final String secret;


    public OtpHasher(String secret) {
        this.secret = secret;
    }

    public String hash(String phone, String code) {
        return HmacUtil.hmacSha256Hex(secret, phone + ":" + code);
    }
}
