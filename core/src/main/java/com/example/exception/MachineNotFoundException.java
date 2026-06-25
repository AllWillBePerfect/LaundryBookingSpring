package com.example.exception;

public class MachineNotFoundException extends LaundryBookingException {

    public MachineNotFoundException(Long machineId) {
        super("Machine not found: " + machineId);
    }
}
