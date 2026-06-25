package com.awbp.exception;

public class SlotBlockedException extends LaundryBookingException {

    public SlotBlockedException() {
        super("Slot is blocked");
    }
}
