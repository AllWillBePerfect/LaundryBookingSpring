package com.example.exception;

public class BlockedSlotNotFoundException extends LaundryBookingException {

    public BlockedSlotNotFoundException(Long id) {
        super("Blocked slot not found: " + id);
    }
}
