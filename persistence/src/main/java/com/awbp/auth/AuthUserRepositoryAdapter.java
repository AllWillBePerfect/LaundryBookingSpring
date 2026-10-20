package com.awbp.auth;


import com.awbp.identity.application.AuthUserRepository;
import com.awbp.identity.domain.AuthProvider;
import com.awbp.identity.domain.UserAccount;
import com.awbp.identity.domain.UserIdentity;
import com.awbp.identity.domain.UserRole;
import com.awbp.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthUserRepositoryAdapter implements AuthUserRepository {
    private final UserJpaRepository userJpa;
    private final UserIdentityJpaRepository identityJpa;

    @Override
    public Optional<UserIdentity> findIdentity(AuthProvider provider, String providerUserId) {
        return identityJpa.findByProviderAndProviderUserId(provider, providerUserId)
                .map(this::toIdentity);
    }

    @Override
    public boolean identityExists(AuthProvider provider, String providerUserId) {
        return identityJpa.existsByProviderAndProviderUserId(provider, providerUserId);
    }

    @Override
    public Optional<UserAccount> findUserById(Long userId) {
        return userJpa.findById(userId).map(this::toAccount);
    }

    @Override
    public List<UserAccount> findAll() {
        return userJpa.findAll().stream().map(this::toAccount).toList();
    }

    @Override
    @Transactional
    public UserAccount createUserWithPassword(String username, String displayName,
                                              String passwordHash, UserRole role) {
        UserJpaEntity user = userJpa.save(UserJpaEntity.builder()
                .displayName(displayName).role(role)
                .createdAt(Instant.now()).build());
        identityJpa.save(UserIdentityJpaEntity.builder()
                .userId(user.getId())
                .provider(AuthProvider.PASSWORD)
                .providerUserId(username)
                .secret(passwordHash)
                .createdAt(Instant.now()).build());
        return toAccount(user);
    }

    @Override
    @Transactional
    public UserAccount createUserWithTelegram(String telegramId, String displayName, UserRole role) {
        UserJpaEntity user = userJpa.save(UserJpaEntity.builder()
                .displayName(displayName).role(role)
                .createdAt(Instant.now()).build());
        identityJpa.save(UserIdentityJpaEntity.builder()
                .userId(user.getId())
                .provider(AuthProvider.TELEGRAM)
                .providerUserId(telegramId)
                .secret(null)
                .createdAt(Instant.now()).build());
        return toAccount(user);
    }

    private UserAccount toAccount(UserJpaEntity e) {
        return new UserAccount(e.getId(), e.getDisplayName(), e.getRole(), e.getCreatedAt());
    }

    private UserIdentity toIdentity(UserIdentityJpaEntity e) {
        return new UserIdentity(e.getId(), e.getUserId(), e.getProvider(),
                e.getProviderUserId(), e.getSecret(), e.getCreatedAt());
    }
}
