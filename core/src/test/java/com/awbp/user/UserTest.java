package com.awbp.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static final Long ID = 1L;
    private static final Long TELEGRAM_ID = 1L;
    private static final String USERNAME = "john";
    private static final String DISPLAY_NAME = "John";
    private static final Instant CREATED_AT = Instant.parse("2026-01-01T09:00:00Z");

    @Test
    void givenValidFields_whenCallConstructor_thenComplete() {
        assertDoesNotThrow(this::validUser);
    }

    @Test
    void givenNullId_whenCallConstructor_thenComplete() {
        assertDoesNotThrow(() -> new User(null, TELEGRAM_ID, USERNAME, DISPLAY_NAME, UserRole.USER, CREATED_AT));
    }

    @Test
    void givenNullTelegramId_whenCallConstructor_thenComplete() {
        assertDoesNotThrow(() -> new User(ID, null, USERNAME, DISPLAY_NAME, UserRole.USER, CREATED_AT));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void givenNonPositiveId_whenCallConstructor_thenThrownIllegalArgumentException(long id) {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> new User(id, TELEGRAM_ID, USERNAME, DISPLAY_NAME, UserRole.USER, CREATED_AT)
        );

        assertTrue(ex.getMessage().contains("id"));
        assertTrue(ex.getMessage().contains("must be positive"));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void givenNonPositiveTelegramId_whenCallConstructor_thenThrownIllegalArgumentException(long telegramId) {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> new User(ID, telegramId, USERNAME, DISPLAY_NAME, UserRole.USER, CREATED_AT)
        );

        assertTrue(ex.getMessage().contains("telegramId"));
        assertTrue(ex.getMessage().contains("must be positive"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void givenBlankUsername_whenCallConstructor_thenThrownIllegalArgumentException(String username) {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> new User(ID, TELEGRAM_ID, username, DISPLAY_NAME, UserRole.USER, CREATED_AT)
        );

        assertTrue(ex.getMessage().contains("username"));
        assertTrue(ex.getMessage().contains("must not be blank"));
    }

    @Test
    void givenNullUsername_whenCallConstructor_thenThrownIllegalArgumentException() {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> new User(ID, TELEGRAM_ID, null, DISPLAY_NAME, UserRole.USER, CREATED_AT)
        );

        assertTrue(ex.getMessage().contains("username"));
        assertTrue(ex.getMessage().contains("must not be blank"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void givenBlankDisplayName_whenCallConstructor_thenThrownIllegalArgumentException(String displayName) {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> new User(ID, TELEGRAM_ID, USERNAME, displayName, UserRole.USER, CREATED_AT)
        );

        assertTrue(ex.getMessage().contains("displayName"));
        assertTrue(ex.getMessage().contains("must not be blank"));
    }

    @Test
    void givenNullDisplayName_whenCallConstructor_thenThrownIllegalArgumentException() {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> new User(ID, TELEGRAM_ID, USERNAME, null, UserRole.USER, CREATED_AT)
        );

        assertTrue(ex.getMessage().contains("displayName"));
        assertTrue(ex.getMessage().contains("must not be blank"));
    }

    @Test
    void givenNullRole_whenCallConstructor_thenThrownNullPointerException() {
        var ex = assertThrows(
                NullPointerException.class,
                () -> new User(ID, TELEGRAM_ID, USERNAME, DISPLAY_NAME, null, CREATED_AT)
        );

        assertTrue(ex.getMessage().contains("role"));
        assertTrue(ex.getMessage().contains("must not be null"));
    }

    @Test
    void givenNullCreatedAt_whenCallConstructor_thenThrownNullPointerException() {
        var ex = assertThrows(
                NullPointerException.class,
                () -> new User(ID, TELEGRAM_ID, USERNAME, DISPLAY_NAME, UserRole.USER, null)
        );

        assertTrue(ex.getMessage().contains("createdAt"));
        assertTrue(ex.getMessage().contains("must not be null"));
    }

    private User validUser() {
        return new User(ID, TELEGRAM_ID, USERNAME, DISPLAY_NAME, UserRole.USER, CREATED_AT);
    }
}
