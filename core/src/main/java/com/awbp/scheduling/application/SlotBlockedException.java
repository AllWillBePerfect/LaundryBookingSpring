package com.awbp.scheduling.application;

import com.awbp.shared.exception.LaundryBookingException;

public class SlotBlockedException extends LaundryBookingException {

    public SlotBlockedException() {
        super("Slot is blocked");
    }
}
