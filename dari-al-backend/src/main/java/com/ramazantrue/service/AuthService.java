package com.ramazantrue.service;

import com.ramazantrue.config.AppConfig;
import com.ramazantrue.domain.SmsCode;
import com.ramazantrue.service.otp.OtpGenerator;
import com.ramazantrue.service.otp.OtpHasher;
import com.ramazantrue.service.sms.SmsSender;
import com.ramazantrue.storage.SmsCodeRepository;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;

public final class AuthService {
    private final DataSource ds;
    private final SmsCodeRepository smsRepo;
    private final OtpGenerator otpGenerator;
    private final OtpHasher otpHasher;
    private final SmsSender smsSender;
    private final int ttlSeconds;
    private final int attempts;

    public AuthService(DataSource ds, SmsCodeRepository smsRepo, OtpGenerator otpGenerator, OtpHasher otpHasher, SmsSender smsSender, int ttlSeconds, int attempts) {
        this.ds = ds;
        this.smsRepo = smsRepo;
        this.otpGenerator = otpGenerator;
        this.otpHasher = otpHasher;
        this.smsSender = smsSender;
        this.ttlSeconds = ttlSeconds;
        this.attempts = attempts;
    }

    public void sendCode(String phone) {
        String code = otpGenerator.getCode();
        String hash = otpHasher.hash(phone, code);
        var now = Instant.now();
        var expiresAt = now.plusSeconds(ttlSeconds);
        try (Connection c = ds.getConnection()){
            smsRepo.create(c, phone, hash, expiresAt, attempts);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        smsSender.send(phone, code);
    }

    public boolean verify(String phone, String code) {
        String hash = otpHasher.hash(phone, code);
        var now = Instant.now();
        try (Connection c = ds.getConnection()) {
            c.setAutoCommit(false);
            var opt = smsRepo.findLatestValid(c, phone, now);
            if(opt.isEmpty()) {
                c.commit();
                return  false;
            }

            SmsCode current = opt.get();

            if(current.codeHash().equals(hash)) {
                smsRepo.markUsed(c, current.id(), now);
                c.commit();
                return true;
            }
            else {
                smsRepo.decreaseAttempts(c, current.id());
                c.commit();
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
