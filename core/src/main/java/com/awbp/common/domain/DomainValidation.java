package com.awbp.common.domain;

import java.util.Objects;

public final class DomainValidation {

    public static void requirePositiveIfPresent(Long value, String fieldName) {
        if (value != null && value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
    }

    public static void requirePositive(Long value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
    }

    public static <T> void requireNonNull(T obj, String fieldValue) {
        Objects.requireNonNull(obj, fieldValue + " must not be null");
    }

    public static void requireNotNullOrBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }


}
