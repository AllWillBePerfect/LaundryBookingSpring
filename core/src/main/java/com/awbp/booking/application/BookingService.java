package com.awbp.booking.application;

import com.awbp.booking.command.CancelBookingCommand;
import com.awbp.booking.command.CreateBookingCommand;
import com.awbp.booking.command.ReviewBookingCommand;
import com.awbp.booking.command.SubmitPaymentCommand;
import com.awbp.booking.domain.Booking;
import com.awbp.booking.domain.BookingStatus;
import com.awbp.scheduling.application.*;
import com.awbp.scheduling.domain.Machine;
import com.awbp.shared.domain.TimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final Duration DEFAULT_SLOT_DURATION = Duration.ofMinutes(90);

    private final MachineRepository machineRepository;
    private final BookingRepository bookingRepository;
    private final BlockedSlotRepository blockedSlotRepository;
    private final Clock clock;

    @Transactional
    public Booking createBooking(CreateBookingCommand command) {
        Machine machine = machineRepository.findById(command.machineId())
                .orElseThrow(() -> new MachineNotFoundException(command.machineId()));

        if (!machine.active()) {
            throw new MachineInactiveException(command.machineId());
        }

        LocalDateTime endTime = command.startTime().plus(DEFAULT_SLOT_DURATION);
        TimeRange timeRange = new TimeRange(command.startTime(), endTime);

        if (blockedSlotRepository.existsOverlapping(command.machineId(), timeRange)) {
            throw new SlotBlockedException();
        }

        if (bookingRepository.existsByMachineIdAndStartTime(command.machineId(), command.startTime())) {
            throw new SlotAlreadyBookedException();
        }

        Instant now = clock.instant();
        Booking booking = new Booking(
                null,
                command.userId(),
                command.machineId(),
                command.startTime(),
                endTime,
                BookingStatus.CREATED,
                now,
                now,
                null,
                null
        );

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancelBooking(CancelBookingCommand command) {
        Booking booking = bookingRepository.findById(command.bookingId())
                .orElseThrow(() -> new BookingNotFoundException(command.bookingId()));

        if (!booking.userId().equals(command.userId())) {
            throw new AccessDeniedToBookingException();
        }

        if (booking.status() != BookingStatus.CREATED
                && booking.status() != BookingStatus.WAITING_PAYMENT_CONFIRMATION) {
            throw new InvalidBookingStateException("Booking cannot be cancelled from status: " + booking.status());
        }

        Booking cancelled = copyWithStatus(booking, BookingStatus.CANCELLED, null, null);
        return bookingRepository.save(cancelled);
    }

    @Transactional
    public Booking submitPayment(SubmitPaymentCommand command) {
        Booking booking = bookingRepository.findById(command.bookingId())
                .orElseThrow(() -> new BookingNotFoundException(command.bookingId()));

        if (!booking.userId().equals(command.userId())) {
            throw new AccessDeniedToBookingException();
        }
        if (booking.status() != BookingStatus.CREATED) {
            throw new InvalidBookingStateException(
                    "Booking cannot be submitted from status: " + booking.status());
        }

        Booking submitted = copyWithStatus(
                booking, BookingStatus.WAITING_PAYMENT_CONFIRMATION, null, null);
        return bookingRepository.save(submitted);
    }

    @Transactional
    public Booking approveBooking(ReviewBookingCommand command) {
        Booking booking = bookingRepository.findById(command.bookingId())
                .orElseThrow(() -> new BookingNotFoundException(command.bookingId()));

        if (booking.status() != BookingStatus.WAITING_PAYMENT_CONFIRMATION) {
            throw new InvalidBookingStateException("Booking cannot be approved from status: " + booking.status());
        }

        Booking approved = copyWithStatus(booking, BookingStatus.APPROVED, clock.instant(), command.adminId());
        return bookingRepository.save(approved);
    }

    @Transactional
    public Booking rejectBooking(ReviewBookingCommand command) {
        Booking booking = bookingRepository.findById(command.bookingId())
                .orElseThrow(() -> new BookingNotFoundException(command.bookingId()));

        if (booking.status() != BookingStatus.WAITING_PAYMENT_CONFIRMATION) {
            throw new InvalidBookingStateException("Booking cannot be rejected from status: " + booking.status());
        }

        Booking rejected = copyWithStatus(booking, BookingStatus.REJECTED, null, command.adminId());
        return bookingRepository.save(rejected);
    }

    @Transactional(readOnly = true)
    public Booking getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
        if (!booking.userId().equals(userId)) {
            throw new AccessDeniedToBookingException();
        }
        return booking;
    }

    @Transactional(readOnly = true)
    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findAllByStatus(status);
    }

    private Booking copyWithStatus(Booking booking, BookingStatus status, Instant approvedAt, Long approvedBy) {
        return new Booking(
                booking.id(),
                booking.userId(),
                booking.machineId(),
                booking.startTime(),
                booking.endTime(),
                status,
                booking.createdAt(),
                clock.instant(),
                approvedAt,
                approvedBy
        );
    }
}
