package com.awbp.booking;

import com.example.booking.domain.Booking;

import java.time.Instant;
import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        Long userId,
        Long machineId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.id(), booking.userId(), booking.machineId(),
                booking.startTime(), booking.endTime(), booking.status().name(),
                booking.createdAt(), booking.updatedAt()
        );
    }
}
