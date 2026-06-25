package com.awbp.booking.command;


import com.awbp.common.domain.DomainValidation;

import java.time.LocalDateTime;

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
