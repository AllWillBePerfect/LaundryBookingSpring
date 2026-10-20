package com.awbp.identity.security;

import com.awbp.identity.domain.UserRole;

public record AuthenticatedUser(Long userId, UserRole role) {
}
