package com.awbp.machine;

import com.awbp.AbstractRepositoryIT;
import com.awbp.scheduling.domain.Machine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MachineRepositoryAdapterIT extends AbstractRepositoryIT {

    private static final Instant CREATED_AT = Instant.parse("2026-01-01T09:00:00Z");

    @Autowired
    private MachineJpaRepository jpaRepository;

    private MachineRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new MachineRepositoryAdapter(jpaRepository);
    }

    @Test
    void givenNewMachine_whenSave_thenAssignsIdAndPersists() {
        Machine saved = adapter.save(machine("Washer", true));

        assertNotNull(saved.id());

        Optional<Machine> found = adapter.findById(saved.id());
        assertTrue(found.isPresent());
        assertEquals("Washer", found.get().name());
        assertTrue(found.get().active());
    }

    @Test
    void givenNoMachine_whenFindById_thenEmpty() {
        assertTrue(adapter.findById(9999L).isEmpty());
    }

    @Test
    void givenActiveAndInactiveMachines_whenFindAllActive_thenReturnsOnlyActive() {
        adapter.save(machine("Active one", true));
        adapter.save(machine("Inactive one", false));

        List<Machine> active = adapter.findAllActive();

        assertEquals(1, active.size());
        assertEquals("Active one", active.get(0).name());
    }

    private Machine machine(String name, boolean active) {
        return new Machine(null, name, "description", active, CREATED_AT);
    }
}
