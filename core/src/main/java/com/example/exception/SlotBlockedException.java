package com.example.exception;

public class SlotBlockedException extends LaundryBookingException {

    public SlotBlockedException() {
        super("Slot is blocked");
    }
}
