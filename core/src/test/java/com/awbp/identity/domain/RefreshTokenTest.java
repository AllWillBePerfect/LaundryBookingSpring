package com.awbp.identity.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {

    private static final Long ID = 1L;
    private static final Long USER_ID = 1L;
    private static final String HASH = "hash";
    private static final Instant NOW = Instant.parse("2026-01-01T09:00:00Z");

    @Test
    void givenActiveTokenWithFutureExpiry_whenIsUsable_thenTrue() {
        RefreshToken token = refreshToken(false, NOW.plusSeconds(60));

        assertTrue(token.isUsable(NOW));
    }

    @Test
    void givenRevokedToken_whenIsUsable_thenFalse() {
        RefreshToken token = refreshToken(true, NOW.plusSeconds(60));

        assertFalse(token.isUsable(NOW));
    }

    @Test
    void givenExpiredToken_whenIsUsable_thenFalse() {
        RefreshToken token = refreshToken(false, NOW.minusSeconds(1));

        assertFalse(token.isUsable(NOW));
    }

    @Test
    void givenExpiryEqualsNow_whenIsUsable_thenFalse() {
        RefreshToken token = refreshToken(false, NOW);

        assertFalse(token.isUsable(NOW));
    }

    private RefreshToken refreshToken(boolean revoked, Instant expiresAt) {
        return new RefreshToken(ID, USER_ID, HASH, expiresAt, revoked, NOW);
    }
}
