package com.awbp.identity.application;

import com.awbp.identity.domain.*;
import com.awbp.exception.InvalidCredentialsException;
import com.awbp.exception.InvalidRefreshTokenException;
import com.awbp.exception.UserAlreadyExistsException;
import com.awbp.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long IDENTITY_ID = 5L;
    private static final Long TOKEN_ID = 9L;
    private static final String USERNAME = "john";
    private static final String DISPLAY_NAME = "John";
    private static final String PASSWORD = "password";
    private static final String STORED_SECRET = "stored-secret";
    private static final String COMPUTED_HASH = "computed-hash";
    private static final String TELEGRAM_ID = "tg-123";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String NEW_RAW_REFRESH = "new-raw-refresh";
    private static final String NEW_HASHED_REFRESH = "new-hashed-refresh";
    private static final String OLD_RAW_REFRESH = "old-raw-refresh";
    private static final String OLD_HASHED_REFRESH = "old-hashed-refresh";
    private static final Duration REFRESH_TTL = Duration.ofDays(7);
    private static final Instant NOW = Instant.parse("2026-01-01T09:00:00Z");

    @Mock
    private AuthUserRepository authUserRepository;
    @Mock
    private PasswordHasher passwordHasher;
    @Mock
    private TokenIssuer tokenIssuer;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private RefreshTokenGenerator refreshGenerator;
    @Mock
    private TokenHasher tokenHasher;

    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);

    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(
                authUserRepository,
                passwordHasher,
                tokenIssuer,
                refreshTokenRepository,
                refreshGenerator,
                tokenHasher,
                REFRESH_TTL,
                clock
        );
    }

    @Test
    void givenUnknownUsername_whenLogin_thenThrownInvalidCredentialsException() {
        when(authUserRepository.findIdentity(AuthProvider.PASSWORD, USERNAME)).thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> service.login(USERNAME, PASSWORD)
        );

        verify(refreshTokenRepository, never()).save(any(), any(), any());
    }

    @Test
    void givenWrongPassword_whenLogin_thenThrownInvalidCredentialsException() {
        when(authUserRepository.findIdentity(AuthProvider.PASSWORD, USERNAME))
                .thenReturn(Optional.of(passwordIdentity()));
        when(passwordHasher.matches(PASSWORD, STORED_SECRET)).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> service.login(USERNAME, PASSWORD)
        );

        verify(refreshTokenRepository, never()).save(any(), any(), any());
    }

    @Test
    void givenMissingAccount_whenLogin_thenThrownInvalidCredentialsException() {
        when(authUserRepository.findIdentity(AuthProvider.PASSWORD, USERNAME))
                .thenReturn(Optional.of(passwordIdentity()));
        when(passwordHasher.matches(PASSWORD, STORED_SECRET)).thenReturn(true);
        when(authUserRepository.findUserById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> service.login(USERNAME, PASSWORD)
        );

        verify(refreshTokenRepository, never()).save(any(), any(), any());
    }

    @Test
    void givenValidCredentials_whenLogin_thenReturnsTokenPairAndStoresRefresh() {
        when(authUserRepository.findIdentity(AuthProvider.PASSWORD, USERNAME))
                .thenReturn(Optional.of(passwordIdentity()));
        when(passwordHasher.matches(PASSWORD, STORED_SECRET)).thenReturn(true);
        when(authUserRepository.findUserById(USER_ID)).thenReturn(Optional.of(userAccount()));
        stubTokenIssuing();

        TokenPair result = service.login(USERNAME, PASSWORD);

        assertEquals(ACCESS_TOKEN, result.accessToken());
        assertEquals(NEW_RAW_REFRESH, result.refreshToken());
        verify(refreshTokenRepository).save(USER_ID, NEW_HASHED_REFRESH, NOW.plus(REFRESH_TTL));
    }

    @Test
    void givenExistingUsername_whenRegister_thenThrownUserAlreadyExistsException() {
        when(authUserRepository.identityExists(AuthProvider.PASSWORD, USERNAME)).thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> service.register(USERNAME, DISPLAY_NAME, PASSWORD)
        );

        verify(authUserRepository, never()).createUserWithPassword(any(), any(), any(), any());
    }

    @Test
    void givenNewUsername_whenRegister_thenCreatesUserAndReturnsTokenPair() {
        when(authUserRepository.identityExists(AuthProvider.PASSWORD, USERNAME)).thenReturn(false);
        when(passwordHasher.hash(PASSWORD)).thenReturn(COMPUTED_HASH);
        when(authUserRepository.createUserWithPassword(USERNAME, DISPLAY_NAME, COMPUTED_HASH, UserRole.USER))
                .thenReturn(userAccount());
        stubTokenIssuing();

        TokenPair result = service.register(USERNAME, DISPLAY_NAME, PASSWORD);

        assertEquals(ACCESS_TOKEN, result.accessToken());
        assertEquals(NEW_RAW_REFRESH, result.refreshToken());
        verify(refreshTokenRepository).save(USER_ID, NEW_HASHED_REFRESH, NOW.plus(REFRESH_TTL));
    }

    @Test
    void givenKnownTelegramId_whenAuthenticateViaTelegram_thenUsesExistingAccount() {
        when(authUserRepository.findIdentity(AuthProvider.TELEGRAM, TELEGRAM_ID))
                .thenReturn(Optional.of(telegramIdentity()));
        when(authUserRepository.findUserById(USER_ID)).thenReturn(Optional.of(userAccount()));
        stubTokenIssuing();

        TokenPair result = service.authenticateViaTelegram(TELEGRAM_ID, DISPLAY_NAME);

        assertEquals(ACCESS_TOKEN, result.accessToken());
        verify(authUserRepository, never()).createUserWithTelegram(any(), any(), any());
    }

    @Test
    void givenNewTelegramId_whenAuthenticateViaTelegram_thenCreatesAccount() {
        when(authUserRepository.findIdentity(AuthProvider.TELEGRAM, TELEGRAM_ID)).thenReturn(Optional.empty());
        when(authUserRepository.createUserWithTelegram(TELEGRAM_ID, DISPLAY_NAME, UserRole.USER))
                .thenReturn(userAccount());
        stubTokenIssuing();

        TokenPair result = service.authenticateViaTelegram(TELEGRAM_ID, DISPLAY_NAME);

        assertEquals(ACCESS_TOKEN, result.accessToken());
        verify(authUserRepository).createUserWithTelegram(TELEGRAM_ID, DISPLAY_NAME, UserRole.USER);
    }

    @Test
    void givenUnknownToken_whenRefresh_thenThrownInvalidRefreshTokenException() {
        when(tokenHasher.hash(OLD_RAW_REFRESH)).thenReturn(OLD_HASHED_REFRESH);
        when(refreshTokenRepository.findByHash(OLD_HASHED_REFRESH)).thenReturn(Optional.empty());

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> service.refresh(OLD_RAW_REFRESH)
        );

        verify(refreshTokenRepository, never()).revoke(any());
    }

    @Test
    void givenRevokedToken_whenRefresh_thenThrownInvalidRefreshTokenException() {
        when(tokenHasher.hash(OLD_RAW_REFRESH)).thenReturn(OLD_HASHED_REFRESH);
        when(refreshTokenRepository.findByHash(OLD_HASHED_REFRESH))
                .thenReturn(Optional.of(refreshToken(true, NOW.plusSeconds(3600))));

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> service.refresh(OLD_RAW_REFRESH)
        );

        verify(refreshTokenRepository, never()).revoke(any());
    }

    @Test
    void givenExpiredToken_whenRefresh_thenThrownInvalidRefreshTokenException() {
        when(tokenHasher.hash(OLD_RAW_REFRESH)).thenReturn(OLD_HASHED_REFRESH);
        when(refreshTokenRepository.findByHash(OLD_HASHED_REFRESH))
                .thenReturn(Optional.of(refreshToken(false, NOW.minusSeconds(1))));

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> service.refresh(OLD_RAW_REFRESH)
        );

        verify(refreshTokenRepository, never()).revoke(any());
    }

    @Test
    void givenMissingAccount_whenRefresh_thenRevokesTokenAndThrows() {
        when(tokenHasher.hash(OLD_RAW_REFRESH)).thenReturn(OLD_HASHED_REFRESH);
        when(refreshTokenRepository.findByHash(OLD_HASHED_REFRESH))
                .thenReturn(Optional.of(refreshToken(false, NOW.plusSeconds(3600))));
        when(authUserRepository.findUserById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> service.refresh(OLD_RAW_REFRESH)
        );

        verify(refreshTokenRepository).revoke(TOKEN_ID);
        verify(refreshTokenRepository, never()).save(any(), any(), any());
    }

    @Test
    void givenUsableToken_whenRefresh_thenRotatesTokenAndReturnsPair() {
        when(tokenHasher.hash(OLD_RAW_REFRESH)).thenReturn(OLD_HASHED_REFRESH);
        when(refreshTokenRepository.findByHash(OLD_HASHED_REFRESH))
                .thenReturn(Optional.of(refreshToken(false, NOW.plusSeconds(3600))));
        when(authUserRepository.findUserById(USER_ID)).thenReturn(Optional.of(userAccount()));
        stubTokenIssuing();

        TokenPair result = service.refresh(OLD_RAW_REFRESH);

        assertEquals(ACCESS_TOKEN, result.accessToken());
        assertEquals(NEW_RAW_REFRESH, result.refreshToken());
        verify(refreshTokenRepository).revoke(TOKEN_ID);
        verify(refreshTokenRepository).save(USER_ID, NEW_HASHED_REFRESH, NOW.plus(REFRESH_TTL));
    }

    @Test
    void givenKnownToken_whenLogout_thenRevokesToken() {
        when(tokenHasher.hash(OLD_RAW_REFRESH)).thenReturn(OLD_HASHED_REFRESH);
        when(refreshTokenRepository.findByHash(OLD_HASHED_REFRESH))
                .thenReturn(Optional.of(refreshToken(false, NOW.plusSeconds(3600))));

        service.logout(OLD_RAW_REFRESH);

        verify(refreshTokenRepository).revoke(TOKEN_ID);
    }

    @Test
    void givenUnknownToken_whenLogout_thenDoesNothing() {
        when(tokenHasher.hash(OLD_RAW_REFRESH)).thenReturn(OLD_HASHED_REFRESH);
        when(refreshTokenRepository.findByHash(OLD_HASHED_REFRESH)).thenReturn(Optional.empty());

        service.logout(OLD_RAW_REFRESH);

        verify(refreshTokenRepository, never()).revoke(any());
    }

    private void stubTokenIssuing() {
        when(tokenIssuer.issue(USER_ID, UserRole.USER)).thenReturn(ACCESS_TOKEN);
        when(refreshGenerator.generate()).thenReturn(NEW_RAW_REFRESH);
        when(tokenHasher.hash(NEW_RAW_REFRESH)).thenReturn(NEW_HASHED_REFRESH);
    }

    private UserAccount userAccount() {
        return new UserAccount(USER_ID, DISPLAY_NAME, UserRole.USER, NOW);
    }

    private UserIdentity passwordIdentity() {
        return new UserIdentity(IDENTITY_ID, USER_ID, AuthProvider.PASSWORD, USERNAME, STORED_SECRET, NOW);
    }

    private UserIdentity telegramIdentity() {
        return new UserIdentity(IDENTITY_ID, USER_ID, AuthProvider.TELEGRAM, TELEGRAM_ID, null, NOW);
    }

    private RefreshToken refreshToken(boolean revoked, Instant expiresAt) {
        return new RefreshToken(TOKEN_ID, USER_ID, OLD_HASHED_REFRESH, expiresAt, revoked, NOW);
    }
}
