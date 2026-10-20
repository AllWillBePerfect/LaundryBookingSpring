package com.awbp.secure;

import com.awbp.AbstractRepositoryIT;
import com.awbp.identity.domain.RefreshToken;
import com.awbp.user.UserJpaEntity;
import com.awbp.user.UserJpaRepository;
import com.awbp.user.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefreshTokenRepositoryAdapterIT extends AbstractRepositoryIT {

    private static final Instant NOW = Instant.parse("2026-01-01T09:00:00Z");

    @Autowired
    private RefreshTokenJpaRepository refreshTokenJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private RefreshTokenRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RefreshTokenRepositoryAdapter(refreshTokenJpaRepository);
    }

    @Test
    void givenTokenData_whenSave_thenPersistsUsableToken() {
        Long userId = seedUser();

        RefreshToken saved = adapter.save(userId, "hash-1", NOW.plusSeconds(3600));

        assertNotNull(saved.id());
        assertEquals(userId, saved.userId());
        assertFalse(saved.revoked());

        Optional<RefreshToken> found = adapter.findByHash("hash-1");
        assertTrue(found.isPresent());
        assertEquals(saved.id(), found.get().id());
    }

    @Test
    void givenNoToken_whenFindByHash_thenEmpty() {
        assertTrue(adapter.findByHash("missing").isEmpty());
    }

    @Test
    void givenToken_whenRevoke_thenMarkedRevoked() {
        Long userId = seedUser();
        RefreshToken saved = adapter.save(userId, "hash-1", NOW.plusSeconds(3600));

        adapter.revoke(saved.id());

        Optional<RefreshToken> found = adapter.findByHash("hash-1");
        assertTrue(found.isPresent());
        assertTrue(found.get().revoked());
    }

    @Test
    void givenMultipleTokens_whenRevokeAllForUser_thenAllRevoked() {
        Long userId = seedUser();
        adapter.save(userId, "hash-1", NOW.plusSeconds(3600));
        adapter.save(userId, "hash-2", NOW.plusSeconds(3600));

        adapter.revokeAllForUser(userId);
        entityManager.clear();

        assertTrue(adapter.findByHash("hash-1").get().revoked());
        assertTrue(adapter.findByHash("hash-2").get().revoked());
    }

    private Long seedUser() {
        return userJpaRepository.save(UserJpaEntity.builder()
                .displayName("John")
                .role(UserRole.USER)
                .createdAt(NOW)
                .build()).getId();
    }
}
