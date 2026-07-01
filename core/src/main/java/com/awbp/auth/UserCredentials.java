package com.awbp.auth;


import com.awbp.user.UserRole;

public record UserCredentials(Long userId, String username, String passwordHash, UserRole role) {
}
