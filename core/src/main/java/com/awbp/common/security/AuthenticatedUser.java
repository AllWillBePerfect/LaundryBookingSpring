package com.awbp.common.security;

import com.awbp.user.UserRole;

public record AuthenticatedUser(Long userId, UserRole role) {
}
