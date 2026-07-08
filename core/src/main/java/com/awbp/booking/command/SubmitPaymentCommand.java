package com.awbp.booking.command;

import com.awbp.common.domain.DomainValidation;

public record SubmitPaymentCommand(Long bookingId, Long userId) {
    public SubmitPaymentCommand {
        DomainValidation.requirePositive(bookingId, "bookingId");
        DomainValidation.requirePositive(userId, "userId");
    }
}