package com.awbp.shared.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DomainValidationTest {

    @Test
    void givenPositiveValue_whenRequirePositiveIfPresent_thenComplete() {
        assertDoesNotThrow(() -> DomainValidation.requirePositiveIfPresent(1L, "userId"));
    }

    @Test
    void givenNullValue_whenRequirePositiveIfPresent_thenComplete() {
        assertDoesNotThrow(() -> DomainValidation.requirePositiveIfPresent(null, "userId"));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void givenNonPositiveValue_whenRequirePositiveIfPresent_thenThrownIllegalArgumentException(long value) {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> DomainValidation.requirePositiveIfPresent(value, "userId")
        );

        assertTrue(ex.getMessage().contains("userId"));
        assertTrue(ex.getMessage().contains("must be positive"));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void givenNonPositiveValue_whenRequirePositive_thenThrownIllegalArgumentException(long value) {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> DomainValidation.requirePositive(value, "userId")
        );

        assertTrue(ex.getMessage().contains("userId"));
        assertTrue(ex.getMessage().contains("must be positive"));
    }

    @Test
    void givenNullValue_whenRequirePositive_thenThrownIllegalArgumentException() {

        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> DomainValidation.requirePositive(null, "userId")
        );

        assertTrue(ex.getMessage().contains("userId"));
        assertTrue(ex.getMessage().contains("must not be null"));
    }

    @Test
    void givenCorrectValue_whenRequirePositive_thenComplete() {
        assertDoesNotThrow(() -> DomainValidation.requirePositive(1L, "userId"));
    }

    @Test
    void givenNullValue_whenRequireNonNull_thenThrownNullPointerException() {

        var ex = assertThrows(
                NullPointerException.class,
                () -> DomainValidation.requireNonNull(null, "userName")
        );

        assertTrue(ex.getMessage().contains("userName"));
        assertTrue(ex.getMessage().contains("must not be null"));
    }

    @Test
    void givenNotNullValue_whenRequireNonNull_thenComplete() {
        assertDoesNotThrow(() -> DomainValidation.requireNonNull("John", "userName"));
    }

    @Test
    void givenNullValue_whenRequireNotNullOrBlank_thenThrownIllegalArgumentException() {

        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> DomainValidation.requireNotNullOrBlank(null, "userName")
        );

        assertTrue(ex.getMessage().contains("userName"));
        assertTrue(ex.getMessage().contains("must not be blank"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void givenBlankValues_whenRequireNotNullOrBlank_thenThrownIllegalArgumentException(String value) {

        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> DomainValidation.requireNotNullOrBlank(value, "userName")
        );

        assertTrue(ex.getMessage().contains("userName"));
        assertTrue(ex.getMessage().contains("must not be blank"));
    }

    @Test
    void givenNonNullValue_whenRequireNotNullOrBlank_thenComplete() {
        assertDoesNotThrow(() -> DomainValidation.requireNotNullOrBlank("John", "userName"));
    }
}