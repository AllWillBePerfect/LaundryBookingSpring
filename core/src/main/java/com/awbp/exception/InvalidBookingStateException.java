package com.awbp.exception;

public class InvalidBookingStateException extends LaundryBookingException {

    public InvalidBookingStateException(String message) {
        super(message);
    }
}
