package com.example.booking.application;

import com.example.booking.domain.Booking;
import com.example.booking.domain.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {

    boolean existsByMachineIdAndStartTime(Long machineId, LocalDateTime startTime);

    Optional<Booking> findById(Long bookingId);

    List<Booking> findAllByUserId(Long userId);

    List<Booking> findAll();

    List<Booking> findAllByStatus(BookingStatus status);

    List<Booking> findAllByMachineIdAndStartTimeBetween(Long machineId, LocalDateTime from, LocalDateTime to);

    Booking save(Booking booking);
}
