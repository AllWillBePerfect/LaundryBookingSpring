package com.awbp.exception;

import com.awbp.shared.exception.LaundryBookingException;

public class BookingNotFoundException extends LaundryBookingException {

    public BookingNotFoundException(Long bookingId) {
        super("Booking not found: " + bookingId);
    }
}
