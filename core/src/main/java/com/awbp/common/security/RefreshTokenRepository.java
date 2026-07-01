package com.awbp.common.security;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository {

    RefreshToken save(Long userId, String tokenHash, Instant expiresAt);
    Optional<RefreshToken> findByHash(String tokenHash);
    void revoke(Long id);
    void revokeAllForUser(Long userId);

}
