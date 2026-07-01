package com.awbp.config;

import com.awbp.auth.AuthUserRepository;
import com.awbp.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seed("admin", "Admin", "admin123", UserRole.ADMIN);
        seed("user",  "Test User", "user123",  UserRole.USER);
    }

    private void seed(String username, String displayName, String rawPassword, UserRole role) {
        if (authUserRepository.existsByUsername(username)) {
            return;
        }
        authUserRepository.createUser(username, displayName, passwordEncoder.encode(rawPassword), role);
    }
}
