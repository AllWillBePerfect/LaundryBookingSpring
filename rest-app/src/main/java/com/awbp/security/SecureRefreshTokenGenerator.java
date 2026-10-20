package com.awbp.security;

import com.awbp.identity.application.RefreshTokenGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class SecureRefreshTokenGenerator implements RefreshTokenGenerator {
    private final SecureRandom random = new SecureRandom();
    @Override
    public String generate() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}