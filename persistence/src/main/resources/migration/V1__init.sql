CREATE TABLE users
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username     VARCHAR(100) NOT NULL,
    display_name VARCHAR(200) NOT NULL,
    role         VARCHAR(20)  NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_users_username UNIQUE (username)
);