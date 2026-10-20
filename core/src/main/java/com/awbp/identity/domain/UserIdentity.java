package com.awbp.identity.domain;

import java.time.Instant;

public record UserIdentity(
        Long id,
        Long userId,
        AuthProvider provider,
        String providerUserId,
        String secret,
        Instant createdAt
) {}
