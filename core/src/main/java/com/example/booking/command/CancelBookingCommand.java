package com.example.booking.command;


import com.example.common.domain.DomainValidation;

public record CancelBookingCommand(Long bookingId, Long userId) {
    public CancelBookingCommand {
        DomainValidation.requirePositive(bookingId, "bookingId");
        DomainValidation.requirePositive(userId, "userId");
    }
}
