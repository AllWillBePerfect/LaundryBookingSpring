package com.awbp.identity.application;

import com.awbp.identity.domain.AuthProvider;
import com.awbp.identity.domain.UserAccount;
import com.awbp.identity.domain.UserIdentity;
import com.awbp.identity.domain.UserRole;

import java.util.List;
import java.util.Optional;

// core
public interface AuthUserRepository {
    Optional<UserIdentity> findIdentity(AuthProvider provider, String providerUserId);
    boolean identityExists(AuthProvider provider, String providerUserId);
    Optional<UserAccount> findUserById(Long userId);
    List<UserAccount> findAll();

    UserAccount createUserWithPassword(String username, String displayName,
                                       String passwordHash, UserRole role);

    UserAccount createUserWithTelegram(String telegramId, String displayName, UserRole role);
}
