package com.awbp.shared.exception;

public abstract class LaundryBookingException extends RuntimeException {

    protected LaundryBookingException(String message) {
        super(message);
    }
}
