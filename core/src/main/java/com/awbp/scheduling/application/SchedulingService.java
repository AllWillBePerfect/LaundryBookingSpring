package com.awbp.scheduling.application;

import com.awbp.scheduling.SchedulingApi;
import com.awbp.scheduling.domain.Machine;
import com.awbp.shared.domain.TimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
class SchedulingService implements SchedulingApi {

    private final MachineRepository machineRepository;
    private final BlockedSlotRepository blockedSlotRepository;

    @Override
    public void ensureMachineAvailable(Long machineId, TimeRange timeRange) {
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new MachineNotFoundException(machineId));

        if (!machine.active()) {
            throw new MachineInactiveException(machineId);
        }

        if (blockedSlotRepository.existsOverlapping(machineId, timeRange)) {
            throw new SlotBlockedException();
        }

    }
}
