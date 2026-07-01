package com.awbp.security;

import com.awbp.auth.TokenIssuer;
import com.awbp.user.UserRole;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenIssuer implements TokenIssuer {
    private final JwtTokenProvider provider;
    public JwtTokenIssuer(JwtTokenProvider provider) { this.provider = provider; }
    public String issue(Long userId, UserRole role) {
        return provider.generateToken(userId, role);
    }
}