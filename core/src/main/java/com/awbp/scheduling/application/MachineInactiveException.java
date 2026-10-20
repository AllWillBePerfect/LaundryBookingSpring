package com.awbp.scheduling.application;

import com.awbp.shared.exception.LaundryBookingException;

public class MachineInactiveException extends LaundryBookingException {

    public MachineInactiveException(Long machineId) {
        super("Machine is inactive: " + machineId);
    }
}
