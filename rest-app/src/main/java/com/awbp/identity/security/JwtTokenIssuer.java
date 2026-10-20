package com.awbp.identity.security;

import com.awbp.identity.application.TokenIssuer;
import com.awbp.identity.domain.UserRole;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenIssuer implements TokenIssuer {
    private final JwtTokenProvider provider;
    public JwtTokenIssuer(JwtTokenProvider provider) { this.provider = provider; }
    public String issue(Long userId, UserRole role) {
        return provider.generateToken(userId, role);
    }
}