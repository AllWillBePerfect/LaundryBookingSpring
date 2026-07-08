package com.awbp.common.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TimeRangeTest {

    private static final LocalDateTime T10 = LocalDateTime.of(2026, 1, 1, 10, 0);
    private static final LocalDateTime T11 = LocalDateTime.of(2026, 1, 1, 11, 0);
    private static final LocalDateTime T12 = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final LocalDateTime T13 = LocalDateTime.of(2026, 1, 1, 13, 0);

    @Test
    void givenPartialOverlap_whenOverlaps_thenTrue() {
        TimeRange a = new TimeRange(T10, T12);
        TimeRange b = new TimeRange(T11, T13);

        boolean result = a.overlaps(b);

        assertThat(result).isTrue();
    }

    @Test
    void givenNestedRanges_whenOverlaps_thenTrue() {
        TimeRange outer = new TimeRange(T10, T13);
        TimeRange inner = new TimeRange(T11, T12);

        assertThat(outer.overlaps(inner)).isTrue();
        assertThat(inner.overlaps(outer)).isTrue();
    }

    @Test
    void givenSeparateRanges_whenOverlaps_thenFalse() {
        TimeRange a = new TimeRange(T10, T11);
        TimeRange b = new TimeRange(T12, T13);

        boolean result = a.overlaps(b);

        assertThat(result).isFalse();
    }

    @Test
    void givenTouchingRanges_whenOverlaps_thenFalse() {
        TimeRange earlier = new TimeRange(T10, T11);
        TimeRange later = new TimeRange(T11, T12);

        assertThat(earlier.overlaps(later)).isFalse();
        assertThat(later.overlaps(earlier)).isFalse();
    }
}