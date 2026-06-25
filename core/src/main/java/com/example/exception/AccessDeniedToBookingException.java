package com.example.exception;

public class AccessDeniedToBookingException extends LaundryBookingException {

    public AccessDeniedToBookingException() {
        super("Access denied to booking");
    }
}
