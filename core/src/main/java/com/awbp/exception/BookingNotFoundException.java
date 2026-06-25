package com.awbp.exception;

public class BookingNotFoundException extends LaundryBookingException {

    public BookingNotFoundException(Long bookingId) {
        super("Booking not found: " + bookingId);
    }
}
