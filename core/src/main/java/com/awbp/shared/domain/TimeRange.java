package com.awbp.shared.domain;

import java.time.LocalDateTime;

public record TimeRange(LocalDateTime start, LocalDateTime end) {

    public boolean overlaps(TimeRange other) {
        return start.isBefore(other.end) && end.isAfter(other.start);
    }
}
