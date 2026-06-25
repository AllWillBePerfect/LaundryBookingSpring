package com.awbp.booking;


import com.awbp.booking.application.BlockedSlotRepository;
import com.awbp.booking.domain.BlockedSlot;
import com.awbp.common.domain.TimeRange;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BlockedSlotRepositoryAdapter implements BlockedSlotRepository {

    private final BlockedSlotJpaRepository jpaRepository;

    public BlockedSlotRepositoryAdapter(BlockedSlotJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existsOverlapping(Long machineId, TimeRange timeRange) {
        return jpaRepository.countOverlapping(machineId, timeRange.start(), timeRange.end()) > 0;
    }

    @Override
    public List<BlockedSlot> findAllOverlapping(Long machineId, TimeRange timeRange) {
        return jpaRepository.findAllOverlapping(machineId, timeRange.start(), timeRange.end())
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<BlockedSlot> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public BlockedSlot save(BlockedSlot blockedSlot) {
        return toDomain(jpaRepository.save(toEntity(blockedSlot)));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private BlockedSlot toDomain(BlockedSlotJpaEntity entity) {
        return new BlockedSlot(
                entity.getId(), entity.getMachineId(),
                entity.getStartTime(), entity.getEndTime(),
                entity.getReason(), entity.getCreatedBy(), entity.getCreatedAt()
        );
    }

    private BlockedSlotJpaEntity toEntity(BlockedSlot blockedSlot) {
        return BlockedSlotJpaEntity.builder()
                .id(blockedSlot.id())
                .machineId(blockedSlot.machineId())
                .startTime(blockedSlot.startTime())
                .endTime(blockedSlot.endTime())
                .reason(blockedSlot.reason())
                .createdBy(blockedSlot.createdBy())
                .createdAt(blockedSlot.createdAt())
                .build();
    }
}
