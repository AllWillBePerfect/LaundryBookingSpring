package com.awbp.booking.application;

import com.awbp.shared.exception.LaundryBookingException;

public class SlotAlreadyBookedException extends LaundryBookingException {

    public SlotAlreadyBookedException() {
        super("Slot is already booked");
    }
}
