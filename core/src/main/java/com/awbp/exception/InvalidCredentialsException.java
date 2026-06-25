package com.awbp.exception;

public class InvalidCredentialsException extends LaundryBookingException {

    public InvalidCredentialsException() {
        super("Invalid username or password");
    }
}
