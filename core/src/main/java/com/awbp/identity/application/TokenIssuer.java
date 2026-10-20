package com.awbp.identity.application;

import com.awbp.user.UserRole;

public interface TokenIssuer {
    String issue(Long userId, UserRole role);
}