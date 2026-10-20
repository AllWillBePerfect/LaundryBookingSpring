package com.awbp.exception;

import com.awbp.shared.exception.LaundryBookingException;

public class InvalidBookingStateException extends LaundryBookingException {

    public InvalidBookingStateException(String message) {
        super(message);
    }
}
