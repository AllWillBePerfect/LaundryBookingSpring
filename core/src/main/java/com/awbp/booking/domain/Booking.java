package com.awbp.booking.domain;

import com.awbp.shared.domain.DomainValidation;
import com.awbp.shared.domain.TimeRange;

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
) {
    public Booking {
        DomainValidation.requirePositiveIfPresent(id, "id");
        DomainValidation.requirePositive(userId, "userId");
        DomainValidation.requirePositive(machineId, "machineId");
        DomainValidation.requireNonNull(startTime, "startTime");
        DomainValidation.requireNonNull(endTime, "endTime");
        DomainValidation.requireNonNull(status, "status");
        DomainValidation.requireNonNull(createdAt, "createdAt");
        DomainValidation.requireNonNull(updatedAt, "updatedAt");

        new TimeRange(startTime, endTime);
    }

    public TimeRange timeRange() {
        return new TimeRange(startTime, endTime);
    }

    public boolean isActive(LocalDateTime now) {
        return endTime.isAfter(now)
                && status != BookingStatus.CANCELLED
                && status != BookingStatus.REJECTED
                && status != BookingStatus.EXPIRED;
    }
}
