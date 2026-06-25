package com.example.exception;

public class BookingNotFoundException extends LaundryBookingException {

    public BookingNotFoundException(Long bookingId) {
        super("Booking not found: " + bookingId);
    }
}
