package com.awbp.booking.command;


import com.awbp.shared.domain.DomainValidation;

public record ReviewBookingCommand(Long bookingId, Long adminId) {

    public ReviewBookingCommand {
        DomainValidation.requirePositive(bookingId, "bookingId");
        DomainValidation.requirePositive(adminId, "adminId");
    }
}
