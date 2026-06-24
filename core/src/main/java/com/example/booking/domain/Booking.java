package com.example.booking.domain;

import java.time.Instant;
import java.time.LocalDateTime;

public record Booking(
        Long id,
        Long userId,
        Long machineId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BookingStatus status,
        Instant createdAt,
        Instant updatedAt,
        Instant approvedAt,
        Long approvedBy
) {}
