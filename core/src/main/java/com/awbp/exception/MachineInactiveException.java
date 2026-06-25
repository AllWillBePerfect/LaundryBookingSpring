package com.awbp.exception;

public class MachineInactiveException extends LaundryBookingException {

    public MachineInactiveException(Long machineId) {
        super("Machine is inactive: " + machineId);
    }
}
