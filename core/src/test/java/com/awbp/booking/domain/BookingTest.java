package com.awbp.booking.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    private static final Long ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long MACHINE_ID = 1L;
    private static final LocalDateTime START = LocalDateTime.of(2026, 1, 1, 10, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 1, 1, 11, 0);
    private static final Instant CREATED = Instant.now();
    private static final Instant UPDATED = Instant.now();
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 1, 1, 12, 0);

    @Test
    void givenValidUser_whenCallConstructor_thenComplete() {
        assertDoesNotThrow(this::validBooking);
    }

    @Test
    void givenNullId_whenCallConstructor_thenComplete() {
        assertDoesNotThrow(() -> new Booking(null, USER_ID, MACHINE_ID, START, END,
                BookingStatus.CREATED, CREATED, UPDATED, null, null));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void givenNonPositiveId_whenCallConstructor_thenThrownIllegalArgumentException(long id) {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Booking(id, USER_ID, MACHINE_ID, START, END,
                        BookingStatus.CREATED, CREATED, UPDATED, null, null)
        );

        assertTrue(ex.getMessage().contains("id"));
        assertTrue(ex.getMessage().contains("must be positive"));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void givenNonPositiveUserId_whenCallConstructor_thenThrownIllegalArgumentException(long userId) {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Booking(ID, userId, MACHINE_ID, START, END,
                        BookingStatus.CREATED, CREATED, UPDATED, null, null)
        );

        assertTrue(ex.getMessage().contains("userId"));
        assertTrue(ex.getMessage().contains("must be positive"));
    }

    @Test
    void givenNullUserId_whenCallConstructor_thenThrownIllegalArgumentException() {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Booking(ID, null, MACHINE_ID, START, END,
                        BookingStatus.CREATED, CREATED, UPDATED, null, null)
        );

        assertTrue(ex.getMessage().contains("userId"));
        assertTrue(ex.getMessage().contains("must not be null"));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void givenNonPositiveMachineId_whenCallConstructor_thenThrownIllegalArgumentException(long machineId) {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Booking(ID, USER_ID, machineId, START, END,
                        BookingStatus.CREATED, CREATED, UPDATED, null, null)
        );

        assertTrue(ex.getMessage().contains("machineId"));
        assertTrue(ex.getMessage().contains("must be positive"));
    }

    @Test
    void givenNullMachineId_whenCallConstructor_thenThrownIllegalArgumentException() {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Booking(ID, USER_ID, null, START, END,
                        BookingStatus.CREATED, CREATED, UPDATED, null, null)
        );

        assertTrue(ex.getMessage().contains("machineId"));
        assertTrue(ex.getMessage().contains("must not be null"));
    }

    @Test
    void givenNullStartTime_whenCallConstructor_thenThrownNullPointerException() {
        var ex = assertThrows(
                NullPointerException.class,
                () -> new Booking(ID, USER_ID, MACHINE_ID, null, END,
                        BookingStatus.CREATED, CREATED, UPDATED, null, null)
        );

        assertTrue(ex.getMessage().contains("startTime"));
        assertTrue(ex.getMessage().contains("must not be null"));
    }

    @Test
    void givenNullEndTime_whenCallConstructor_thenThrownNullPointerException() {
        var ex = assertThrows(
                NullPointerException.class,
                () -> new Booking(ID, USER_ID, MACHINE_ID, START, null,
                        BookingStatus.CREATED, CREATED, UPDATED, null, null)
        );

        assertTrue(ex.getMessage().contains("endTime"));
        assertTrue(ex.getMessage().contains("must not be null"));
    }

    @Test
    void givenNullStatus_whenCallConstructor_thenThrownNullPointerException() {
        var ex = assertThrows(
                NullPointerException.class,
                () -> new Booking(ID, USER_ID, MACHINE_ID, START, END,
                        null, CREATED, UPDATED, null, null)
        );

        assertTrue(ex.getMessage().contains("status"));
        assertTrue(ex.getMessage().contains("must not be null"));
    }

    @Test
    void givenNullCreatedAt_whenCallConstructor_thenThrownNullPointerException() {
        var ex = assertThrows(
                NullPointerException.class,
                () -> new Booking(ID, USER_ID, MACHINE_ID, START, END,
                        BookingStatus.CREATED, null, UPDATED, null, null)
        );

        assertTrue(ex.getMessage().contains("createdAt"));
        assertTrue(ex.getMessage().contains("must not be null"));
    }

    @Test
    void givenNullUpdatedAt_whenCallConstructor_thenThrownNullPointerException() {
        var ex = assertThrows(
                NullPointerException.class,
                () -> new Booking(ID, USER_ID, MACHINE_ID, START, END,
                        BookingStatus.CREATED, CREATED, null, null, null)
        );

        assertTrue(ex.getMessage().contains("updatedAt"));
        assertTrue(ex.getMessage().contains("must not be null"));
    }

    @Test
    void givenBooking_whenTimeRange_thenReturnsStartAndEnd() {
        var range = validBooking().timeRange();

        assertEquals(START, range.start());
        assertEquals(END, range.end());
    }

    @ParameterizedTest
    @EnumSource(value = BookingStatus.class, names = {"CREATED", "WAITING_PAYMENT_CONFIRMATION", "APPROVED"})
    void givenFutureEndAndActiveStatus_whenIsActive_thenTrue(BookingStatus status) {
        var booking = bookingWith(status, NOW.plusHours(1));

        assertTrue(booking.isActive(NOW));
    }

    @ParameterizedTest
    @EnumSource(value = BookingStatus.class, names = {"CANCELLED", "REJECTED", "EXPIRED"})
    void givenFutureEndAndInactiveStatus_whenIsActive_thenFalse(BookingStatus status) {
        var booking = bookingWith(status, NOW.plusHours(1));

        assertFalse(booking.isActive(NOW));
    }

    @Test
    void givenPastEnd_whenIsActive_thenFalse() {
        var booking = bookingWith(BookingStatus.CREATED, NOW.minusHours(1));

        assertFalse(booking.isActive(NOW));
    }

    @Test
    void givenEndEqualsNow_whenIsActive_thenFalse() {
        var booking = bookingWith(BookingStatus.CREATED, NOW);

        assertFalse(booking.isActive(NOW));
    }

    private Booking validBooking() {
        return new Booking(ID, USER_ID, MACHINE_ID, START, END,
                BookingStatus.CREATED, CREATED, UPDATED, null, null);
    }

    private Booking bookingWith(BookingStatus status, LocalDateTime endTime) {
        return new Booking(ID, USER_ID, MACHINE_ID, START, endTime,
                status, CREATED, UPDATED, null, null);
    }
}
