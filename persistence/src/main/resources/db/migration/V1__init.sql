-- CREATE TABLE users
-- (
--     id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
--     username      VARCHAR(100) NOT NULL,
--     display_name  VARCHAR(200) NOT NULL,
--     password_hash VARCHAR(255) NOT NULL,
--     role          VARCHAR(20)  NOT NULL,
--     created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
--     CONSTRAINT uq_users_username UNIQUE (username)
-- );

CREATE TABLE users
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    display_name VARCHAR(200) NOT NULL,
    role         VARCHAR(20)  NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE user_identities
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id          BIGINT       NOT NULL,
    provider         VARCHAR(20)  NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    secret           VARCHAR(255),
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_identity_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uq_identity UNIQUE (provider, provider_user_id)
);
CREATE INDEX idx_identity_user_id ON user_identities (user_id);

CREATE TABLE machines
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE blocked_slots
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    machine_id BIGINT       NOT NULL,
    start_time TIMESTAMP    NOT NULL,
    end_time   TIMESTAMP    NOT NULL,
    reason     VARCHAR(500) NOT NULL,
    created_by BIGINT       NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_blocked_machine FOREIGN KEY (machine_id) REFERENCES machines (id)
);
CREATE INDEX idx_blocked_machine ON blocked_slots (machine_id);

CREATE TABLE bookings
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id     BIGINT      NOT NULL,
    machine_id  BIGINT      NOT NULL,
    start_time  TIMESTAMP   NOT NULL,
    end_time    TIMESTAMP   NOT NULL,
    status      VARCHAR(40) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL,
    approved_at TIMESTAMPTZ,
    approved_by BIGINT,
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_bookings_machine FOREIGN KEY (machine_id) REFERENCES machines (id),
    CONSTRAINT fk_bookings_approved_by FOREIGN KEY (approved_by) REFERENCES users (id),
    CONSTRAINT uq_bookings_machine_start UNIQUE (machine_id, start_time)
);

CREATE INDEX idx_bookings_machine_start ON bookings (machine_id, start_time);
CREATE INDEX idx_bookings_user_id ON bookings (user_id);
CREATE INDEX idx_bookings_status ON bookings (status);


CREATE TABLE refresh_tokens
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    token_hash  VARCHAR(255) NOT NULL,
    expires_at  TIMESTAMPTZ  NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uq_refresh_token_hash UNIQUE (token_hash)
);
CREATE INDEX idx_refresh_user_id ON refresh_tokens (user_id);
