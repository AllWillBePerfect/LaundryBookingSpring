package com.example.exception;

public class UserAlreadyExistsException extends LaundryBookingException {

    public UserAlreadyExistsException(String username) {
        super("User already exists: " + username);
    }
}
