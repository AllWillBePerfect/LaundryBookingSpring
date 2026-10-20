package com.awbp.booking.application;

import com.awbp.shared.exception.LaundryBookingException;

public class AccessDeniedToBookingException extends LaundryBookingException {

    public AccessDeniedToBookingException() {
        super("Access denied to booking");
    }
}
