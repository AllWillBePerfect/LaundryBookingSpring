package com.awbp.scheduling.domain;

import com.awbp.shared.domain.DomainValidation;
import com.awbp.shared.domain.TimeRange;

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

    public BlockedSlot {
        DomainValidation.requirePositiveIfPresent(id, "id");
        DomainValidation.requirePositive(machineId, "machineId");
        DomainValidation.requireNonNull(startTime, "startTime");
        DomainValidation.requireNonNull(endTime, "endTime");
        DomainValidation.requireNotNullOrBlank(reason, "reason");
        DomainValidation.requirePositive(createdBy, "createdBy");
        DomainValidation.requireNonNull(createdAt, "createdAt");

        new TimeRange(startTime, endTime);
    }

    public TimeRange timeRange() {
        return new TimeRange(startTime, endTime);
    }

}
