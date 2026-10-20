package com.awbp.exception;

import com.awbp.shared.exception.LaundryBookingException;

public class SlotAlreadyBookedException extends LaundryBookingException {

    public SlotAlreadyBookedException() {
        super("Slot is already booked");
    }
}
