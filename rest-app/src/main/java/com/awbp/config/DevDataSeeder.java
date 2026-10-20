package com.awbp.config;

import com.awbp.identity.domain.AuthProvider;
import com.awbp.identity.application.AuthUserRepository;
import com.awbp.scheduling.application.MachineRepository;
import com.awbp.scheduling.domain.Machine;
import com.awbp.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {

    private final AuthUserRepository authUserRepository;
    private final MachineRepository machineRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedMachines();
    }

    private void seed(String username, String displayName, String rawPassword, UserRole role) {
        if (!authUserRepository.identityExists(AuthProvider.PASSWORD, username)) {
            authUserRepository.createUserWithPassword(
                    username, displayName, passwordEncoder.encode(rawPassword), role);
        }
    }

    private void seedUsers() {
        if (!authUserRepository.findAll().isEmpty()) {
            seed("admin", "Admin", "admin123", UserRole.ADMIN);
            seed("user",  "Test User", "user123",  UserRole.USER);
        }
    }

    private void seedMachines() {
        if (!machineRepository.findAllActive().isEmpty()) {
            return;
        }
        machineRepository.save(new Machine(null, "Washer #1", "Ground floor", true, Instant.now()));
        machineRepository.save(new Machine(null, "Washer #2", "Second floor", true, Instant.now()));
    }
}
