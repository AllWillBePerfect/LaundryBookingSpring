package com.awbp.scheduling.application;

import com.awbp.shared.exception.LaundryBookingException;

public class BlockedSlotNotFoundException extends LaundryBookingException {

    public BlockedSlotNotFoundException(Long id) {
        super("Blocked slot not found: " + id);
    }
}
