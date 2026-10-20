package com.awbp.identity.application;

import com.awbp.shared.exception.LaundryBookingException;

public class InvalidCredentialsException extends LaundryBookingException {

    public InvalidCredentialsException() {
        super("Invalid username or password");
    }
}
