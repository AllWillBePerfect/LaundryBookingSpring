package com.example.booking.domain;

import java.time.Instant;
import java.time.LocalDateTime;

public record BlockedSlot(
        Long id,
        Long machineId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String reason,
        Long createdBy,
        Instant createdAt
) {
}
