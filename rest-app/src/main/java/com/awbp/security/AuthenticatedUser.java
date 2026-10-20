package com.awbp.security;

import com.awbp.identity.domain.UserRole;

public record AuthenticatedUser(Long userId, UserRole role) {
}
