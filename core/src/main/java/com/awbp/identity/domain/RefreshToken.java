package com.awbp.identity.domain;

import java.time.Instant;

public record RefreshToken(
        Long id,
        Long userId,
        String tokenHash,
        Instant expiresAt,
        boolean revoked,
        Instant createdAt
) {
    public boolean isUsable(Instant now) {
        return !revoked && expiresAt.isAfter(now);
    }
}
