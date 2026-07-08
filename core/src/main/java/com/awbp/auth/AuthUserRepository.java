package com.awbp.auth;

import com.awbp.user.UserRole;

import java.util.List;
import java.util.Optional;

/*public interface AuthUserRepository {

    Optional<UserCredentials> findByUsername(String username);

    Optional<UserCredentials> findById(Long userId);

    boolean existsByUsername(String username);

    UserCredentials createUser(String username, String displayName, String passwordHash, UserRole role);
}*/

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
