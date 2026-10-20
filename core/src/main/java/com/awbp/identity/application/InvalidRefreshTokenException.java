package com.awbp.identity.application;

import com.awbp.shared.exception.LaundryBookingException;

public class InvalidRefreshTokenException extends LaundryBookingException {
    public InvalidRefreshTokenException() { super("Invalid refresh token"); }
}
