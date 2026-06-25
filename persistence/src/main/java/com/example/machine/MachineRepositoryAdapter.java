package com.example.machine;


import com.example.booking.machine.application.MachineRepository;
import com.example.booking.machine.domain.Machine;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MachineRepositoryAdapter implements MachineRepository {

    private final MachineJpaRepository jpaRepository;

    public MachineRepositoryAdapter(MachineJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Machine> findById(Long machineId) {
        return jpaRepository.findById(machineId).map(this::toDomain);
    }

    @Override
    public List<Machine> findAllActive() {
        return jpaRepository.findAllByActiveTrue().stream().map(this::toDomain).toList();
    }

    @Override
    public Machine save(Machine machine) {
        return toDomain(jpaRepository.save(toEntity(machine)));
    }

    private Machine toDomain(MachineJpaEntity entity) {
        return new Machine(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt()
        );
    }

    private MachineJpaEntity toEntity(Machine machine) {
        return MachineJpaEntity.builder()
                .id(machine.id())
                .name(machine.name())
                .description(machine.description())
                .active(machine.active())
                .createdAt(machine.createdAt())
                .build();
    }
}
