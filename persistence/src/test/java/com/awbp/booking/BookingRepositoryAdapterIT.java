package com.awbp.booking;

import com.awbp.AbstractRepositoryIT;
import com.awbp.booking.domain.Booking;
import com.awbp.booking.domain.BookingStatus;
import com.awbp.booking.application.SlotAlreadyBookedException;
import com.awbp.scheduling.MachineJpaEntity;
import com.awbp.scheduling.MachineJpaRepository;
import com.awbp.identity.UserJpaEntity;
import com.awbp.identity.UserJpaRepository;
import com.awbp.identity.domain.UserRole;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingRepositoryAdapterIT extends AbstractRepositoryIT {

    private static final LocalDateTime START = LocalDateTime.of(2026, 1, 1, 10, 0);
    private static final Instant NOW = Instant.parse("2026-01-01T09:00:00Z");

    @Autowired
    private BookingJpaRepository bookingJpaRepository;
    @Autowired
    private MachineJpaRepository machineJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    private BookingRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BookingRepositoryAdapter(bookingJpaRepository);
    }

    @Test
    void givenNewBooking_whenSave_thenAssignsIdAndPersists() {
        Long userId = seedUser();
        Long machineId = seedMachine();

        Booking saved = adapter.save(booking(userId, machineId, START, BookingStatus.CREATED));

        assertNotNull(saved.id());

        Optional<Booking> found = adapter.findById(saved.id());
        assertTrue(found.isPresent());
        assertEquals(userId, found.get().userId());
        assertEquals(machineId, found.get().machineId());
        assertEquals(START, found.get().startTime());
        assertEquals(BookingStatus.CREATED, found.get().status());
    }

    @Test
    void givenBookingAtSlot_whenExistsByMachineIdAndStartTime_thenReflectsPresence() {
        Long userId = seedUser();
        Long machineId = seedMachine();
        adapter.save(booking(userId, machineId, START, BookingStatus.CREATED));

        assertTrue(adapter.existsByMachineIdAndStartTime(machineId, START));
        assertFalse(adapter.existsByMachineIdAndStartTime(machineId, START.plusHours(5)));
    }

    @Test
    void givenBookingsOfDifferentUsers_whenFindAllByUserId_thenReturnsOnlyThatUser() {
        Long userA = seedUser();
        Long userB = seedUser();
        Long machineId = seedMachine();
        adapter.save(booking(userA, machineId, START, BookingStatus.CREATED));
        adapter.save(booking(userB, machineId, START.plusHours(2), BookingStatus.CREATED));

        List<Booking> result = adapter.findAllByUserId(userA);

        assertEquals(1, result.size());
        assertEquals(userA, result.get(0).userId());
    }

    @Test
    void givenBookingsWithDifferentStatuses_whenFindAllByStatus_thenFilters() {
        Long userId = seedUser();
        Long machineId = seedMachine();
        adapter.save(booking(userId, machineId, START, BookingStatus.CREATED));
        adapter.save(booking(userId, machineId, START.plusHours(2), BookingStatus.APPROVED));

        List<Booking> approved = adapter.findAllByStatus(BookingStatus.APPROVED);

        assertEquals(1, approved.size());
        assertEquals(BookingStatus.APPROVED, approved.get(0).status());
    }

    @Test
    void givenExistingSlot_whenSaveDuplicate_thenThrownSlotAlreadyBookedException() {
        Long userId = seedUser();
        Long machineId = seedMachine();
        adapter.save(booking(userId, machineId, START, BookingStatus.CREATED));

        assertThrows(
                SlotAlreadyBookedException.class,
                () -> adapter.save(booking(userId, machineId, START, BookingStatus.CREATED))
        );
    }

    private Long seedUser() {
        return userJpaRepository.save(UserJpaEntity.builder()
                .displayName("John")
                .role(UserRole.USER)
                .createdAt(NOW)
                .build()).getId();
    }

    private Long seedMachine() {
        return machineJpaRepository.save(MachineJpaEntity.builder()
                .name("Washer")
                .description("description")
                .active(true)
                .createdAt(NOW)
                .build()).getId();
    }

    private Booking booking(Long userId, Long machineId, LocalDateTime startTime, BookingStatus status) {
        return new Booking(null, userId, machineId, startTime, startTime.plusMinutes(90),
                status, NOW, NOW, null, null);
    }
}
