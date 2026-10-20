package com.awbp.scheduling.application;

import com.awbp.scheduling.domain.Machine;
import com.awbp.shared.domain.TimeRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchedulingServiceTest {

    private static final Long MACHINE_ID = 10L;
    private static final LocalDateTime START = LocalDateTime.of(2026, 1, 1, 10, 0);
    private static final TimeRange TIME_RANGE = new TimeRange(START, START.plusMinutes(90));
    private static final Instant NOW = Instant.parse("2026-01-01T09:00:00Z");

    @Mock
    private MachineRepository machineRepository;
    @Mock
    private BlockedSlotRepository blockedSlotRepository;

    private SchedulingService service;

    @BeforeEach
    void setUp() {
        service = new SchedulingService(machineRepository, blockedSlotRepository);
    }

    @Test
    void givenMissingMachine_whenEnsureMachineAvailable_thenThrownMachineNotFoundException() {
        when(machineRepository.findById(MACHINE_ID)).thenReturn(Optional.empty());

        assertThrows(
                MachineNotFoundException.class,
                () -> service.ensureMachineAvailable(MACHINE_ID, TIME_RANGE)
        );
    }

    @Test
    void givenInactiveMachine_whenEnsureMachineAvailable_thenThrownMachineInactiveException() {
        when(machineRepository.findById(MACHINE_ID)).thenReturn(Optional.of(inactiveMachine()));

        assertThrows(
                MachineInactiveException.class,
                () -> service.ensureMachineAvailable(MACHINE_ID, TIME_RANGE)
        );
    }

    @Test
    void givenBlockedSlot_whenEnsureMachineAvailable_thenThrownSlotBlockedException() {
        when(machineRepository.findById(MACHINE_ID)).thenReturn(Optional.of(activeMachine()));
        when(blockedSlotRepository.existsOverlapping(MACHINE_ID, TIME_RANGE)).thenReturn(true);

        assertThrows(
                SlotBlockedException.class,
                () -> service.ensureMachineAvailable(MACHINE_ID, TIME_RANGE)
        );
    }

    @Test
    void givenActiveMachineAndFreeSlot_whenEnsureMachineAvailable_thenNoException() {
        when(machineRepository.findById(MACHINE_ID)).thenReturn(Optional.of(activeMachine()));
        when(blockedSlotRepository.existsOverlapping(MACHINE_ID, TIME_RANGE)).thenReturn(false);

        assertDoesNotThrow(() -> service.ensureMachineAvailable(MACHINE_ID, TIME_RANGE));
    }

    private Machine activeMachine() {
        return new Machine(MACHINE_ID, "Machine", "description", true, NOW);
    }

    private Machine inactiveMachine() {
        return new Machine(MACHINE_ID, "Machine", "description", false, NOW);
    }
}
