package com.example.exception;

public class SlotAlreadyBookedException extends LaundryBookingException {

    public SlotAlreadyBookedException() {
        super("Slot is already booked");
    }
}
