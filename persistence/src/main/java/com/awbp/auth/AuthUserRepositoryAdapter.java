package com.awbp.auth;


import com.awbp.user.UserJpaEntity;
import com.awbp.user.UserJpaRepository;
import com.awbp.user.UserRole;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class AuthUserRepositoryAdapter implements AuthUserRepository {

    private final UserJpaRepository userJpaRepository;

    public AuthUserRepositoryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<UserCredentials> findById(Long userId) {
        return userJpaRepository.findById(userId).map(this::toCredentials);
    }

    @Override
    public Optional<UserCredentials> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(this::toCredentials);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.findByUsername(username).isPresent();
    }

    @Override
    public UserCredentials createUser(String username, String displayName, String passwordHash, UserRole role) {
        UserJpaEntity entity = UserJpaEntity.builder()
                .username(username)
                .displayName(displayName)
                .passwordHash(passwordHash)
                .role(role)
                .createdAt(Instant.now())
                .build();
        return toCredentials(userJpaRepository.save(entity));
    }

    private UserCredentials toCredentials(UserJpaEntity entity) {
        return new UserCredentials(entity.getId(), entity.getUsername(), entity.getPasswordHash(), entity.getRole());
    }
}
