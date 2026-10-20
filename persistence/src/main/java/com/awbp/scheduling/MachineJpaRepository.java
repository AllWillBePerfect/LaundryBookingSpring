package com.awbp.scheduling;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MachineJpaRepository extends JpaRepository<MachineJpaEntity, Long> {

    List<MachineJpaEntity> findAllByActiveTrue();
}
