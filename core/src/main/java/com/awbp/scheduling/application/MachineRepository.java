package com.awbp.scheduling.application;

import com.awbp.scheduling.domain.Machine;

import java.util.List;
import java.util.Optional;

public interface MachineRepository {

    Optional<Machine> findById(Long machineId);

    List<Machine> findAllActive();

    Machine save(Machine machine);
}
