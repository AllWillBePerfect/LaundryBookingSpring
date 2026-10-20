package com.awbp.identity.domain;


import com.awbp.shared.domain.DomainValidation;

import java.time.Instant;

public record User(
        Long id,
        Long telegramId,
        String username,
        String displayName,
        UserRole role,
        Instant createdAt
) {


    public User {
        DomainValidation.requirePositiveIfPresent(id, "id");
        DomainValidation.requirePositiveIfPresent(telegramId, "telegramId");
        DomainValidation.requireNotNullOrBlank(username, "username");
        DomainValidation.requireNotNullOrBlank(displayName,"displayName");
        DomainValidation.requireNonNull(role, "role");
        DomainValidation.requireNonNull(createdAt, "createdAt");
    }
}
