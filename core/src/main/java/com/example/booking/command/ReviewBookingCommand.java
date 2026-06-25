package com.example.booking.command;


import com.example.common.domain.DomainValidation;

public record ReviewBookingCommand(Long bookingId, Long adminId) {

    public ReviewBookingCommand {
        DomainValidation.requirePositive(bookingId, "bookingId");
        DomainValidation.requirePositive(adminId, "adminId");
    }
}
