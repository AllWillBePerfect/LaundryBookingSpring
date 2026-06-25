package com.awbp.booking.command;


import com.awbp.common.domain.DomainValidation;

public record CancelBookingCommand(Long bookingId, Long userId) {
    public CancelBookingCommand {
        DomainValidation.requirePositive(bookingId, "bookingId");
        DomainValidation.requirePositive(userId, "userId");
    }
}
