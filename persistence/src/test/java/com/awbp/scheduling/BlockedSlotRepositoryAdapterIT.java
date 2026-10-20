package com.awbp.scheduling;

import com.awbp.AbstractRepositoryIT;
import com.awbp.scheduling.domain.BlockedSlot;
import com.awbp.common.domain.TimeRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockedSlotRepositoryAdapterIT extends AbstractRepositoryIT {

    private static final LocalDateTime BLOCK_START = LocalDateTime.of(2026, 1, 1, 10, 0);
    private static final LocalDateTime BLOCK_END = LocalDateTime.of(2026, 1, 1, 11, 30);
    private static final Long CREATED_BY = 1L;
    private static final Instant NOW = Instant.parse("2026-01-01T09:00:00Z");

    @Autowired
    private BlockedSlotJpaRepository blockedSlotJpaRepository;
    @Autowired
    private MachineJpaRepository machineJpaRepository;

    private BlockedSlotRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BlockedSlotRepositoryAdapter(blockedSlotJpaRepository);
    }

    @Test
    void givenNewBlockedSlot_whenSave_thenAssignsIdAndPersists() {
        Long machineId = seedMachine();

        BlockedSlot saved = adapter.save(blockedSlot(machineId, BLOCK_START, BLOCK_END));

        assertNotNull(saved.id());

        Optional<BlockedSlot> found = adapter.findById(saved.id());
        assertTrue(found.isPresent());
        assertEquals(machineId, found.get().machineId());
        assertEquals(BLOCK_START, found.get().startTime());
    }

    @Test
    void givenNoSlot_whenFindById_thenEmpty() {
        assertTrue(adapter.findById(9999L).isEmpty());
    }

    @Test
    void givenPartiallyOverlappingRange_whenExistsOverlapping_thenTrue() {
        Long machineId = seedMachine();
        adapter.save(blockedSlot(machineId, BLOCK_START, BLOCK_END));

        TimeRange query = new TimeRange(LocalDateTime.of(2026, 1, 1, 11, 0), LocalDateTime.of(2026, 1, 1, 12, 0));

        assertTrue(adapter.existsOverlapping(machineId, query));
    }

    @Test
    void givenRangeTouchingBoundary_whenExistsOverlapping_thenFalse() {
        Long machineId = seedMachine();
        adapter.save(blockedSlot(machineId, BLOCK_START, BLOCK_END));

        TimeRange query = new TimeRange(BLOCK_END, LocalDateTime.of(2026, 1, 1, 12, 30));

        assertFalse(adapter.existsOverlapping(machineId, query));
    }

    @Test
    void givenDisjointRange_whenExistsOverlapping_thenFalse() {
        Long machineId = seedMachine();
        adapter.save(blockedSlot(machineId, BLOCK_START, BLOCK_END));

        TimeRange query = new TimeRange(LocalDateTime.of(2026, 1, 1, 12, 0), LocalDateTime.of(2026, 1, 1, 13, 0));

        assertFalse(adapter.existsOverlapping(machineId, query));
    }

    @Test
    void givenOverlapOnOtherMachine_whenExistsOverlapping_thenFalse() {
        Long machineId = seedMachine();
        Long otherMachineId = seedMachine();
        adapter.save(blockedSlot(machineId, BLOCK_START, BLOCK_END));

        TimeRange query = new TimeRange(LocalDateTime.of(2026, 1, 1, 11, 0), LocalDateTime.of(2026, 1, 1, 12, 0));

        assertFalse(adapter.existsOverlapping(otherMachineId, query));
    }

    @Test
    void givenOverlappingSlot_whenFindAllOverlapping_thenReturnsIt() {
        Long machineId = seedMachine();
        adapter.save(blockedSlot(machineId, BLOCK_START, BLOCK_END));

        TimeRange query = new TimeRange(LocalDateTime.of(2026, 1, 1, 11, 0), LocalDateTime.of(2026, 1, 1, 12, 0));
        List<BlockedSlot> result = adapter.findAllOverlapping(machineId, query);

        assertEquals(1, result.size());
        assertEquals(machineId, result.get(0).machineId());
    }

    @Test
    void givenBlockedSlot_whenDeleteById_thenRemoved() {
        Long machineId = seedMachine();
        BlockedSlot saved = adapter.save(blockedSlot(machineId, BLOCK_START, BLOCK_END));

        adapter.deleteById(saved.id());

        assertTrue(adapter.findById(saved.id()).isEmpty());
    }

    private Long seedMachine() {
        return machineJpaRepository.save(MachineJpaEntity.builder()
                .name("Washer")
                .description("description")
                .active(true)
                .createdAt(NOW)
                .build()).getId();
    }

    private BlockedSlot blockedSlot(Long machineId, LocalDateTime start, LocalDateTime end) {
        return new BlockedSlot(null, machineId, start, end, "maintenance", CREATED_BY, NOW);
    }
}
