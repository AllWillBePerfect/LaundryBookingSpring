package com.example.common.security;

import com.example.user.UserRole;

public record AuthenticatedUser(Long userId, UserRole role) {
}
