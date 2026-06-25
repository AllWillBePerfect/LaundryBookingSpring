package com.awbp.exception;

public class BlockedSlotNotFoundException extends LaundryBookingException {

    public BlockedSlotNotFoundException(Long id) {
        super("Blocked slot not found: " + id);
    }
}
