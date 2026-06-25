package com.awbp.exception;

public class MachineNotFoundException extends LaundryBookingException {

    public MachineNotFoundException(Long machineId) {
        super("Machine not found: " + machineId);
    }
}
