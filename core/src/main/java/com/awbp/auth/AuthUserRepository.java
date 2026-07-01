package com.awbp.auth;

import com.awbp.user.UserRole;

import java.util.Optional;

public interface AuthUserRepository {

    Optional<UserCredentials> findByUsername(String username);

    Optional<UserCredentials> findById(Long userId);

    boolean existsByUsername(String username);

    UserCredentials createUser(String username, String displayName, String passwordHash, UserRole role);
}
