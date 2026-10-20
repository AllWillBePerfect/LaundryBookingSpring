package com.awbp.secure;

import com.awbp.identity.domain.RefreshToken;
import com.awbp.identity.application.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

// persistence: адаптер порта
@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository jpa;

    @Override
    public RefreshToken save(Long userId, String tokenHash, Instant expiresAt) {
        var saved = jpa.save(RefreshTokenJpaEntity.builder()
                .userId(userId).tokenHash(tokenHash)
                .expiresAt(expiresAt).revoked(false)
                .createdAt(Instant.now()).build());
        return toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByHash(String tokenHash) {
        return jpa.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    public void revoke(Long id) {
        jpa.findById(id).ifPresent(e -> { e.setRevoked(true); jpa.save(e); });
    }

    @Override
    public void revokeAllForUser(Long userId) {
        jpa.revokeAllByUserId(userId);
    }

    private RefreshToken toDomain(RefreshTokenJpaEntity e) {
        return new RefreshToken(e.getId(), e.getUserId(), e.getTokenHash(),
                e.getExpiresAt(), e.isRevoked(), e.getCreatedAt());
    }
}