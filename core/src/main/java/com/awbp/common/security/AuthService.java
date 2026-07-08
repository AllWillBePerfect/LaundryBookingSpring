package com.awbp.common.security;

import com.awbp.auth.*;
import com.awbp.exception.InvalidCredentialsException;
import com.awbp.exception.InvalidRefreshTokenException;
import com.awbp.exception.UserAlreadyExistsException;
import com.awbp.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

//@Service
//@RequiredArgsConstructor
//public class AuthService {
//
//    private final AuthUserRepository authUserRepository;
//    private final PasswordHasher passwordHasher;
//    private final TokenIssuer tokenIssuer;
//    private final RefreshTokenRepository refreshTokenRepository;
//    private final RefreshTokenGenerator refreshGenerator;
//    private final TokenHasher tokenHasher;
//    private final Duration refreshTtl;
//    private final Clock clock;
//
//
//    public TokenPair register(String username, String displayName, String password) {
//        if (authUserRepository.existsByUsername(username)) {
//            throw new UserAlreadyExistsException(username);
//        }
//        String hash = passwordHasher.hash(password);
//        UserCredentials user = authUserRepository.createUser(
//                username, displayName, hash, UserRole.USER);
//        return issuePair(user.userId(), user.role());
//    }
//
//    public TokenPair login(String username, String password) {
//        UserCredentials user = authUserRepository.findByUsername(username)
//                .orElseThrow(InvalidCredentialsException::new);
//        if (!passwordHasher.matches(password, user.passwordHash())) {
//            throw new InvalidCredentialsException();
//        }
//        return issuePair(user.userId(), user.role());
//    }
//
//    public TokenPair refresh(String rawRefreshToken) {
//        String hash = tokenHasher.hash(rawRefreshToken);
//        RefreshToken stored = refreshTokenRepository.findByHash(hash)
//                .orElseThrow(InvalidRefreshTokenException::new);
//
//        if (!stored.isUsable(clock.instant())) {
//            throw new InvalidRefreshTokenException();
//        }
//
//        refreshTokenRepository.revoke(stored.id());
//
//        UserCredentials user = authUserRepository.findById(stored.userId())
//                .orElseThrow(InvalidRefreshTokenException::new);
//        return issuePair(user.userId(), user.role());
//    }
//
//    public void logout(String rawRefreshToken) {
//        String hash = tokenHasher.hash(rawRefreshToken);
//        refreshTokenRepository.findByHash(hash)
//                .ifPresent(rt -> refreshTokenRepository.revoke(rt.id()));
//    }
//
//    private TokenPair issuePair(Long userId, UserRole role) {
//        String access = tokenIssuer.issue(userId, role);
//        String rawRefresh = refreshGenerator.generate();
//        Instant expiresAt = clock.instant().plus(refreshTtl);
//        refreshTokenRepository.save(userId, tokenHasher.hash(rawRefresh), expiresAt);
//        return new TokenPair(access, rawRefresh);
//    }
//}


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordHasher passwordHasher;
    private final TokenIssuer tokenIssuer;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenGenerator refreshGenerator;
    private final TokenHasher tokenHasher;
    private final Duration refreshTtl;
    private final Clock clock;

    public TokenPair login(String username, String password) {
        UserIdentity identity = authUserRepository
                .findIdentity(AuthProvider.PASSWORD, username)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHasher.matches(password, identity.secret())) {
            throw new InvalidCredentialsException();
        }

        UserAccount user = authUserRepository.findUserById(identity.userId())
                .orElseThrow(InvalidCredentialsException::new);
        return issuePair(user.id(), user.role());
    }

    public TokenPair register(String username, String displayName, String password) {
        if (authUserRepository.identityExists(AuthProvider.PASSWORD, username)) {
            throw new UserAlreadyExistsException(username);
        }
        String hash = passwordHasher.hash(password);
        UserAccount user = authUserRepository
                .createUserWithPassword(username, displayName, hash, UserRole.USER);
        return issuePair(user.id(), user.role());
    }

    public TokenPair authenticateViaTelegram(String telegramId, String displayName) {
        UserAccount user = authUserRepository.findIdentity(AuthProvider.TELEGRAM, telegramId)
                .map(id -> authUserRepository.findUserById(id.userId()).orElseThrow())
                .orElseGet(() -> authUserRepository.createUserWithTelegram(
                        telegramId, displayName, UserRole.USER));
        return issuePair(user.id(), user.role());
    }

    public TokenPair refresh(String rawRefreshToken) {
        String hash = tokenHasher.hash(rawRefreshToken);
        RefreshToken stored = refreshTokenRepository.findByHash(hash)
                .orElseThrow(InvalidRefreshTokenException::new);
        if (!stored.isUsable(clock.instant())) {
            throw new InvalidRefreshTokenException();
        }
        refreshTokenRepository.revoke(stored.id());
        UserAccount user = authUserRepository.findUserById(stored.userId())
                .orElseThrow(InvalidRefreshTokenException::new);
        return issuePair(user.id(), user.role());
    }

    public void logout(String rawRefreshToken) {
        String hash = tokenHasher.hash(rawRefreshToken);
        refreshTokenRepository.findByHash(hash)
                .ifPresent(rt -> refreshTokenRepository.revoke(rt.id()));
    }

        private TokenPair issuePair(Long userId, UserRole role) {
        String access = tokenIssuer.issue(userId, role);
        String rawRefresh = refreshGenerator.generate();
        Instant expiresAt = clock.instant().plus(refreshTtl);
        refreshTokenRepository.save(userId, tokenHasher.hash(rawRefresh), expiresAt);
        return new TokenPair(access, rawRefresh);
    }
}