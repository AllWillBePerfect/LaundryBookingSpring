package com.awbp.identity.application;

import com.awbp.shared.exception.LaundryBookingException;

public class UserAlreadyExistsException extends LaundryBookingException {

    public UserAlreadyExistsException(String username) {
        super("User already exists: " + username);
    }
}
