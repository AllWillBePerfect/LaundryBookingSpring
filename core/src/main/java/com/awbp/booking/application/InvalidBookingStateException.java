package com.awbp.booking.application;

import com.awbp.shared.exception.LaundryBookingException;

public class InvalidBookingStateException extends LaundryBookingException {

    public InvalidBookingStateException(String message) {
        super(message);
    }
}
