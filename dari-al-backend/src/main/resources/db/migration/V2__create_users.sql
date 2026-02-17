CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL PRIMARY KEY,
    phone       VARCHAR(20) NOT NULL UNIQUE,
    name        VARCHAR(255),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    role        VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);

