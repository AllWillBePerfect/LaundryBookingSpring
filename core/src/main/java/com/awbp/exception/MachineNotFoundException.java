package com.awbp.exception;

import com.awbp.shared.exception.LaundryBookingException;

public class MachineNotFoundException extends LaundryBookingException {

    public MachineNotFoundException(Long machineId) {
        super("Machine not found: " + machineId);
    }
}
