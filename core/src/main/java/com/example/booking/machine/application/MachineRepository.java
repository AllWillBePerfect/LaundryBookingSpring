package com.example.booking.machine.application;

import com.example.booking.machine.domain.Machine;

import java.util.List;
import java.util.Optional;

public interface MachineRepository {

    Optional<Machine> findById(Long machineId);

    List<Machine> findAllActive();

    Machine save(Machine machine);
}
