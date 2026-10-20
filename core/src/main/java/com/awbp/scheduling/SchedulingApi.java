package com.awbp.scheduling;

import com.awbp.shared.domain.TimeRange;

public interface SchedulingApi {
    void ensureMachineAvailable(Long machineId, TimeRange timeRange);
}
