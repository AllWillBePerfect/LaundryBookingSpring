package com.awbp.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateBookingRequest(
        @NotNull Long machineId,
        @NotNull @Future LocalDateTime startTime
) {
}
