package com.awbp.booking;

import com.awbp.booking.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<BookingJpaEntity, Long> {

    boolean existsByMachineIdAndStartTime(Long machineId, LocalDateTime startTime);

    List<BookingJpaEntity> findAllByUserId(Long userId);

    List<BookingJpaEntity> findAllByStatus(BookingStatus status);

    List<BookingJpaEntity> findAllByMachineIdAndStartTimeBetween(Long machineId, LocalDateTime from, LocalDateTime to);
}