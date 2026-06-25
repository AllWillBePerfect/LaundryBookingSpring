package com.awbp.booking;

import com.awbp.booking.application.BookingRepository;
import com.awbp.booking.domain.Booking;
import com.awbp.booking.domain.BookingStatus;
import com.awbp.exception.SlotAlreadyBookedException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookingRepositoryAdapter implements BookingRepository {

    private static final String UNIQUE_BOOKING_CONSTRAINT = "uq_bookings_machine_start";

    private final BookingJpaRepository jpaRepository;

    @Override
    public boolean existsByMachineIdAndStartTime(Long machineId, LocalDateTime startTime) {
        return jpaRepository.existsByMachineIdAndStartTime(machineId, startTime);
    }

    @Override
    public Optional<Booking> findById(Long bookingId) {
        return jpaRepository.findById(bookingId).map(this::toDomain);
    }

    @Override
    public List<Booking> findAllByUserId(Long userId) {
        return jpaRepository.findAllByUserId(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Booking> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Booking> findAllByStatus(BookingStatus status) {
        return jpaRepository.findAllByStatus(status).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Booking> findAllByMachineIdAndStartTimeBetween(Long machineId, LocalDateTime from, LocalDateTime to) {
        return jpaRepository.findAllByMachineIdAndStartTimeBetween(machineId, from, to)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Booking save(Booking booking) {
        try {
            return toDomain(jpaRepository.saveAndFlush(toEntity(booking)));
        } catch (DataIntegrityViolationException e) {
            if (isUniqueBookingConstraintViolation(e)) {
                throw new SlotAlreadyBookedException();
            }
            throw e;
        }
    }

    private boolean isUniqueBookingConstraintViolation(DataIntegrityViolationException e) {
        Throwable cause = e.getMostSpecificCause();
        return cause.getMessage() != null && cause.getMessage().contains(UNIQUE_BOOKING_CONSTRAINT);
    }

    private Booking toDomain(BookingJpaEntity entity) {
        return new Booking(
                entity.getId(),
                entity.getUserId(),
                entity.getMachineId(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getApprovedAt(),
                entity.getApprovedBy()
        );
    }

    private BookingJpaEntity toEntity(Booking booking) {
        return BookingJpaEntity.builder()
                .id(booking.id())
                .userId(booking.userId())
                .machineId(booking.machineId())
                .startTime(booking.startTime())
                .endTime(booking.endTime())
                .status(booking.status())
                .createdAt(booking.createdAt())
                .updatedAt(booking.updatedAt())
                .approvedAt(booking.approvedAt())
                .approvedBy(booking.approvedBy())
                .build();
    }
}
