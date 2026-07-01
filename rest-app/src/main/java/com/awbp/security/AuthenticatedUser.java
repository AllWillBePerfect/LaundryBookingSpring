package com.awbp.security;

import com.awbp.user.UserRole;

public record AuthenticatedUser(Long userId, UserRole role) {
}
