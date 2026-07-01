package com.awbp.exception;

public class InvalidRefreshTokenException extends LaundryBookingException{
    public InvalidRefreshTokenException() { super("Invalid refresh token"); }
}
