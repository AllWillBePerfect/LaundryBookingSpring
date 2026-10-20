package com.awbp.scheduling;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BlockedSlotJpaRepository extends JpaRepository<BlockedSlotJpaEntity, Long> {

    @Query("""
            SELECT COUNT(b) FROM BlockedSlotJpaEntity b
            WHERE b.machineId = :machineId
              AND b.startTime < :endTime
              AND b.endTime > :startTime
            """)
    long countOverlapping(
            @Param("machineId") Long machineId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("""
            SELECT b FROM BlockedSlotJpaEntity b
            WHERE b.machineId = :machineId
              AND b.startTime < :endTime
              AND b.endTime > :startTime
            """)
    List<BlockedSlotJpaEntity> findAllOverlapping(
            @Param("machineId") Long machineId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
