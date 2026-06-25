package com.awbp.exception;

public class UserAlreadyExistsException extends LaundryBookingException {

    public UserAlreadyExistsException(String username) {
        super("User already exists: " + username);
    }
}
