-- Таблица для хранения SMS-кодов (OTP). Сам код не хранится — только хеш.
CREATE TABLE sms_codes (
    id          BIGSERIAL PRIMARY KEY,
    phone       VARCHAR(20) NOT NULL,
    code_hash   VARCHAR(256) NOT NULL,
    expires_at  TIMESTAMPTZ NOT NULL,
    attempts_left INT NOT NULL,
    used_at     TIMESTAMPTZ NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_sms_codes_phone_created_at ON sms_codes (phone, created_at DESC);
