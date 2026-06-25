package com.example.booking.application;


import com.example.booking.domain.BlockedSlot;
import com.example.common.domain.TimeRange;

import java.util.List;
import java.util.Optional;

public interface BlockedSlotRepository {

    boolean existsOverlapping(Long machineId, TimeRange timeRange);

    List<BlockedSlot> findAllOverlapping(Long machineId, TimeRange timeRange);

    Optional<BlockedSlot> findById(Long id);

    BlockedSlot save(BlockedSlot blockedSlot);

    void deleteById(Long id);
}
