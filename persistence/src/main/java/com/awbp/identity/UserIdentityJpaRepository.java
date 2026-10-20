package com.awbp.identity;

import com.awbp.identity.domain.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserIdentityJpaRepository extends JpaRepository<UserIdentityJpaEntity, Long> {
    Optional<UserIdentityJpaEntity> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);
    boolean existsByProviderAndProviderUserId(AuthProvider provider, String providerUserId);
}