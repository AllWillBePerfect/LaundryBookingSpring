package com.awbp.identity.domain;

import java.time.Instant;

public record UserAccount(
        Long id,
        String displayName,
        UserRole role,
        Instant createdAt
) {}
