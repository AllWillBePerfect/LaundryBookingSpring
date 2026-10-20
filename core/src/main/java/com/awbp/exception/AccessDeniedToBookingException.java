package com.awbp.exception;

import com.awbp.shared.exception.LaundryBookingException;

public class AccessDeniedToBookingException extends LaundryBookingException {

    public AccessDeniedToBookingException() {
        super("Access denied to booking");
    }
}
