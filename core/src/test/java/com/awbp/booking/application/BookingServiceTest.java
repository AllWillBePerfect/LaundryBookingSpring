package com.awbp.booking.application;

import com.awbp.booking.command.CancelBookingCommand;
import com.awbp.booking.command.CreateBookingCommand;
import com.awbp.booking.command.ReviewBookingCommand;
import com.awbp.booking.command.SubmitPaymentCommand;
import com.awbp.booking.domain.Booking;
import com.awbp.booking.domain.BookingStatus;
import com.awbp.scheduling.application.BlockedSlotRepository;
import com.awbp.scheduling.application.MachineRepository;
import com.awbp.scheduling.domain.Machine;
import com.awbp.shared.domain.TimeRange;
import com.awbp.scheduling.application.MachineInactiveException;
import com.awbp.scheduling.application.MachineNotFoundException;
import com.awbp.scheduling.application.SlotBlockedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
    private static final Long MACHINE_ID = 10L;
    private static final Long BOOKING_ID = 100L;
    private static final Long ADMIN_ID = 7L;
    private static final LocalDateTime START = LocalDateTime.of(2026, 1, 1, 10, 0);
    private static final LocalDateTime END = START.plusMinutes(90);
    private static final Instant NOW = Instant.parse("2026-01-01T09:00:00Z");

    @Mock
    private MachineRepository machineRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BlockedSlotRepository blockedSlotRepository;

    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);

    private BookingService service;

    @BeforeEach
    void setUp() {
        service = new BookingService(machineRepository, bookingRepository, blockedSlotRepository, clock);
    }

    @Test
    void givenMissingMachine_whenCreateBooking_thenThrownMachineNotFoundException() {
        when(machineRepository.findById(MACHINE_ID)).thenReturn(Optional.empty());

        assertThrows(
                MachineNotFoundException.class,
                () -> service.createBooking(new CreateBookingCommand(USER_ID, MACHINE_ID, START))
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void givenInactiveMachine_whenCreateBooking_thenThrownMachineInactiveException() {
        when(machineRepository.findById(MACHINE_ID)).thenReturn(Optional.of(inactiveMachine()));

        assertThrows(
                MachineInactiveException.class,
                () -> service.createBooking(new CreateBookingCommand(USER_ID, MACHINE_ID, START))
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void givenBlockedSlot_whenCreateBooking_thenThrownSlotBlockedException() {
        when(machineRepository.findById(MACHINE_ID)).thenReturn(Optional.of(activeMachine()));
        when(blockedSlotRepository.existsOverlapping(eq(MACHINE_ID), any(TimeRange.class))).thenReturn(true);

        assertThrows(
                SlotBlockedException.class,
                () -> service.createBooking(new CreateBookingCommand(USER_ID, MACHINE_ID, START))
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void givenAlreadyBookedSlot_whenCreateBooking_thenThrownSlotAlreadyBookedException() {
        when(machineRepository.findById(MACHINE_ID)).thenReturn(Optional.of(activeMachine()));
        when(blockedSlotRepository.existsOverlapping(eq(MACHINE_ID), any(TimeRange.class))).thenReturn(false);
        when(bookingRepository.existsByMachineIdAndStartTime(MACHINE_ID, START)).thenReturn(true);

        assertThrows(
                SlotAlreadyBookedException.class,
                () -> service.createBooking(new CreateBookingCommand(USER_ID, MACHINE_ID, START))
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void givenValidCommand_whenCreateBooking_thenSavesBookingWithCreatedStatus() {
        when(machineRepository.findById(MACHINE_ID)).thenReturn(Optional.of(activeMachine()));
        when(blockedSlotRepository.existsOverlapping(eq(MACHINE_ID), any(TimeRange.class))).thenReturn(false);
        when(bookingRepository.existsByMachineIdAndStartTime(MACHINE_ID, START)).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = service.createBooking(new CreateBookingCommand(USER_ID, MACHINE_ID, START));

        assertNull(result.id());
        assertEquals(USER_ID, result.userId());
        assertEquals(MACHINE_ID, result.machineId());
        assertEquals(START, result.startTime());
        assertEquals(END, result.endTime());
        assertEquals(BookingStatus.CREATED, result.status());
        assertEquals(NOW, result.createdAt());
        assertEquals(NOW, result.updatedAt());
    }

    @Test
    void givenMissingBooking_whenCancelBooking_thenThrownBookingNotFoundException() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());

        assertThrows(
                BookingNotFoundException.class,
                () -> service.cancelBooking(new CancelBookingCommand(BOOKING_ID, USER_ID))
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void givenForeignBooking_whenCancelBooking_thenThrownAccessDeniedToBookingException() {
        when(bookingRepository.findById(BOOKING_ID))
                .thenReturn(Optional.of(bookingWithStatus(USER_ID, BookingStatus.CREATED)));

        assertThrows(
                AccessDeniedToBookingException.class,
                () -> service.cancelBooking(new CancelBookingCommand(BOOKING_ID, OTHER_USER_ID))
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void givenApprovedBooking_whenCancelBooking_thenThrownInvalidBookingStateException() {
        when(bookingRepository.findById(BOOKING_ID))
                .thenReturn(Optional.of(bookingWithStatus(USER_ID, BookingStatus.APPROVED)));

        assertThrows(
                InvalidBookingStateException.class,
                () -> service.cancelBooking(new CancelBookingCommand(BOOKING_ID, USER_ID))
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void givenCreatedBooking_whenCancelBooking_thenStatusCancelled() {
        when(bookingRepository.findById(BOOKING_ID))
                .thenReturn(Optional.of(bookingWithStatus(USER_ID, BookingStatus.CREATED)));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = service.cancelBooking(new CancelBookingCommand(BOOKING_ID, USER_ID));

        assertEquals(BookingStatus.CANCELLED, result.status());
    }

    @Test
    void givenWaitingPaymentBooking_whenCancelBooking_thenStatusCancelled() {
        when(bookingRepository.findById(BOOKING_ID))
                .thenReturn(Optional.of(bookingWithStatus(USER_ID, BookingStatus.WAITING_PAYMENT_CONFIRMATION)));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = service.cancelBooking(new CancelBookingCommand(BOOKING_ID, USER_ID));

        assertEquals(BookingStatus.CANCELLED, result.status());
    }

    @Test
    void givenMissingBooking_whenSubmitPayment_thenThrownBookingNotFoundException() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());

        assertThrows(
                BookingNotFoundException.class,
                () -> service.submitPayment(new SubmitPaymentCommand(BOOKING_ID, USER_ID))
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void givenForeignBooking_whenSubmitPayment_thenThrownAccessDeniedToBookingException() {
        when(bookingRepository.findById(BOOKING_ID))
                .thenReturn(Optional.of(bookingWithStatus(USER_ID, BookingStatus.CREATED)));

        assertThrows(
                AccessDeniedToBookingException.class,
                () -> service.submitPayment(new SubmitPaymentCommand(BOOKING_ID, OTHER_USER_ID))
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void givenApprovedBooking_whenSubmitPayment_thenThrownInvalidBookingStateException() {
        when(bookingRepository.findById(BOOKING_ID))
                .thenReturn(Optional.of(bookingWithStatus(USER_ID, BookingStatus.APPROVED)));

        assertThrows(
                InvalidBookingStateException.class,
                () -> service.submitPayment(new SubmitPaymentCommand(BOOKING_ID, USER_ID))
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void givenCreatedBooking_whenSubmitPayment_thenStatusWaitingPayment() {
        when(bookingRepository.findById(BOOKING_ID))
                .thenReturn(Optional.of(bookingWithStatus(USER_ID, BookingStatus.CREATED)));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = service.submitPayment(new SubmitPaymentCommand(BOOKING_ID, USER_ID));

        assertEquals(BookingStatus.WAITING_PAYMENT_CONFIRMATION, result.status());
    }

    @Test
    void givenMissingBooking_whenApproveBooking_thenThrownBookingNotFoundException() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());

        assertThrows(
                BookingNotFoundException.class,
                () -> service.approveBooking(new ReviewBookingCommand(BOOKING_ID, ADMIN_ID))
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void givenCreatedBooking_whenApproveBooking_thenThrownInvalidBookingStateException() {
        when(bookingRepository.findById(BOOKING_ID))
                .thenReturn(Optional.of(bookingWithStatus(USER_ID, BookingStatus.CREATED)));

        assertThrows(
                InvalidBookingStateException.class,
                () -> service.approveBooking(new ReviewBookingCommand(BOOKING_ID, ADMIN_ID))
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void givenWaitingPaymentBooking_whenApproveBooking_thenStatusApproved() {
        when(bookingRepository.findById(BOOKING_ID))
                .thenReturn(Optional.of(bookingWithStatus(USER_ID, BookingStatus.WAITING_PAYMENT_CONFIRMATION)));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = service.approveBooking(new ReviewBookingCommand(BOOKING_ID, ADMIN_ID));

        assertEquals(BookingStatus.APPROVED, result.status());
        assertEquals(NOW, result.approvedAt());
        assertEquals(ADMIN_ID, result.approvedBy());
    }

    @Test
    void givenMissingBooking_whenRejectBooking_thenThrownBookingNotFoundException() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());

        assertThrows(
                BookingNotFoundException.class,
                () -> service.rejectBooking(new ReviewBookingCommand(BOOKING_ID, ADMIN_ID))
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void givenCreatedBooking_whenRejectBooking_thenThrownInvalidBookingStateException() {
        when(bookingRepository.findById(BOOKING_ID))
                .thenReturn(Optional.of(bookingWithStatus(USER_ID, BookingStatus.CREATED)));

        assertThrows(
                InvalidBookingStateException.class,
                () -> service.rejectBooking(new ReviewBookingCommand(BOOKING_ID, ADMIN_ID))
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void givenWaitingPaymentBooking_whenRejectBooking_thenStatusRejected() {
        when(bookingRepository.findById(BOOKING_ID))
                .thenReturn(Optional.of(bookingWithStatus(USER_ID, BookingStatus.WAITING_PAYMENT_CONFIRMATION)));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = service.rejectBooking(new ReviewBookingCommand(BOOKING_ID, ADMIN_ID));

        assertEquals(BookingStatus.REJECTED, result.status());
        assertNull(result.approvedAt());
        assertEquals(ADMIN_ID, result.approvedBy());
    }

    @Test
    void givenMissingBooking_whenGetBooking_thenThrownBookingNotFoundException() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());

        assertThrows(
                BookingNotFoundException.class,
                () -> service.getBooking(BOOKING_ID, USER_ID)
        );
    }

    @Test
    void givenForeignBooking_whenGetBooking_thenThrownAccessDeniedToBookingException() {
        when(bookingRepository.findById(BOOKING_ID))
                .thenReturn(Optional.of(bookingWithStatus(USER_ID, BookingStatus.CREATED)));

        assertThrows(
                AccessDeniedToBookingException.class,
                () -> service.getBooking(BOOKING_ID, OTHER_USER_ID)
        );
    }

    @Test
    void givenOwnBooking_whenGetBooking_thenReturnsBooking() {
        Booking booking = bookingWithStatus(USER_ID, BookingStatus.CREATED);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        Booking result = service.getBooking(BOOKING_ID, USER_ID);

        assertEquals(booking, result);
    }

    @Test
    void givenUserBookings_whenGetUserBookings_thenReturnsList() {
        List<Booking> bookings = List.of(bookingWithStatus(USER_ID, BookingStatus.CREATED));
        when(bookingRepository.findAllByUserId(USER_ID)).thenReturn(bookings);

        assertEquals(bookings, service.getUserBookings(USER_ID));
    }

    @Test
    void givenBookings_whenGetAllBookings_thenReturnsList() {
        List<Booking> bookings = List.of(bookingWithStatus(USER_ID, BookingStatus.CREATED));
        when(bookingRepository.findAll()).thenReturn(bookings);

        assertEquals(bookings, service.getAllBookings());
    }

    @Test
    void givenStatus_whenGetBookingsByStatus_thenReturnsList() {
        List<Booking> bookings = List.of(bookingWithStatus(USER_ID, BookingStatus.APPROVED));
        when(bookingRepository.findAllByStatus(BookingStatus.APPROVED)).thenReturn(bookings);

        assertEquals(bookings, service.getBookingsByStatus(BookingStatus.APPROVED));
    }

    private Machine activeMachine() {
        return new Machine(MACHINE_ID, "Machine", "description", true, NOW);
    }

    private Machine inactiveMachine() {
        return new Machine(MACHINE_ID, "Machine", "description", false, NOW);
    }

    private Booking bookingWithStatus(Long userId, BookingStatus status) {
        return new Booking(BOOKING_ID, userId, MACHINE_ID, START, END, status, NOW, NOW, null, null);
    }
}
