package com.awbp.scheduling.domain;

import java.time.Instant;

public record Machine(
        Long id,
        String name,
        String description,
        boolean active,
        Instant createdAt
) {
}
