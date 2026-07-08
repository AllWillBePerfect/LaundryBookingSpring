package com.awbp.booking.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BlockedSlotTest {

    private static final Long ID = 1L;
    private static final Long MACHINE_ID = 1L;
    private static final LocalDateTime START = LocalDateTime.of(2026, 1, 1, 10, 0);
    private static final LocalDateTime END = START.plusMinutes(90);
    private static final String REASON = "maintenance";
    private static final Long CREATED_BY = 1L;
    private static final Instant CREATED_AT = Instant.parse("2026-01-01T09:00:00Z");

    @Test
    void givenValidFields_whenCallConstructor_thenComplete() {
        assertDoesNotThrow(this::validBlockedSlot);
    }

    @Test
    void givenNullId_whenCallConstructor_thenComplete() {
        assertDoesNotThrow(() -> new BlockedSlot(null, MACHINE_ID, START, END, REASON, CREATED_BY, CREATED_AT));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void givenNonPositiveMachineId_whenCallConstructor_thenThrownIllegalArgumentException(long machineId) {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> new BlockedSlot(ID, machineId, START, END, REASON, CREATED_BY, CREATED_AT)
        );

        assertTrue(ex.getMessage().contains("machineId"));
        assertTrue(ex.getMessage().contains("must be positive"));
    }

    @Test
    void givenNullMachineId_whenCallConstructor_thenThrownIllegalArgumentException() {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> new BlockedSlot(ID, null, START, END, REASON, CREATED_BY, CREATED_AT)
        );

        assertTrue(ex.getMessage().contains("machineId"));
        assertTrue(ex.getMessage().contains("must not be null"));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void givenNonPositiveCreatedBy_whenCallConstructor_thenThrownIllegalArgumentException(long createdBy) {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> new BlockedSlot(ID, MACHINE_ID, START, END, REASON, createdBy, CREATED_AT)
        );

        assertTrue(ex.getMessage().contains("createdBy"));
        assertTrue(ex.getMessage().contains("must be positive"));
    }

    @Test
    void givenNullCreatedBy_whenCallConstructor_thenThrownIllegalArgumentException() {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> new BlockedSlot(ID, MACHINE_ID, START, END, REASON, null, CREATED_AT)
        );

        assertTrue(ex.getMessage().contains("createdBy"));
        assertTrue(ex.getMessage().contains("must not be null"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void givenBlankReason_whenCallConstructor_thenThrownIllegalArgumentException(String reason) {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> new BlockedSlot(ID, MACHINE_ID, START, END, reason, CREATED_BY, CREATED_AT)
        );

        assertTrue(ex.getMessage().contains("reason"));
        assertTrue(ex.getMessage().contains("must not be blank"));
    }

    @Test
    void givenNullReason_whenCallConstructor_thenThrownIllegalArgumentException() {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> new BlockedSlot(ID, MACHINE_ID, START, END, null, CREATED_BY, CREATED_AT)
        );

        assertTrue(ex.getMessage().contains("reason"));
        assertTrue(ex.getMessage().contains("must not be blank"));
    }

    @Test
    void givenNullStartTime_whenCallConstructor_thenThrownNullPointerException() {
        var ex = assertThrows(
                NullPointerException.class,
                () -> new BlockedSlot(ID, MACHINE_ID, null, END, REASON, CREATED_BY, CREATED_AT)
        );

        assertTrue(ex.getMessage().contains("startTime"));
        assertTrue(ex.getMessage().contains("must not be null"));
    }

    @Test
    void givenNullEndTime_whenCallConstructor_thenThrownNullPointerException() {
        var ex = assertThrows(
                NullPointerException.class,
                () -> new BlockedSlot(ID, MACHINE_ID, START, null, REASON, CREATED_BY, CREATED_AT)
        );

        assertTrue(ex.getMessage().contains("endTime"));
        assertTrue(ex.getMessage().contains("must not be null"));
    }

    @Test
    void givenNullCreatedAt_whenCallConstructor_thenThrownNullPointerException() {
        var ex = assertThrows(
                NullPointerException.class,
                () -> new BlockedSlot(ID, MACHINE_ID, START, END, REASON, CREATED_BY, null)
        );

        assertTrue(ex.getMessage().contains("createdAt"));
        assertTrue(ex.getMessage().contains("must not be null"));
    }

    @Test
    void givenBlockedSlot_whenTimeRange_thenReturnsStartAndEnd() {
        var range = validBlockedSlot().timeRange();

        assertEquals(START, range.start());
        assertEquals(END, range.end());
    }

    private BlockedSlot validBlockedSlot() {
        return new BlockedSlot(ID, MACHINE_ID, START, END, REASON, CREATED_BY, CREATED_AT);
    }
}
