package com.example.booking.command;


import com.example.common.domain.DomainValidation;

import java.time.LocalDateTime;
import java.util.Objects;

public record CreateBookingCommand(
        Long userId,
        Long machineId,
        LocalDateTime startTime
) {

    public CreateBookingCommand {
        DomainValidation.requirePositive(userId, "userId");
        DomainValidation.requirePositive(machineId, "machineId");
        DomainValidation.requireNonNull(startTime, "startTime");
    }
}
