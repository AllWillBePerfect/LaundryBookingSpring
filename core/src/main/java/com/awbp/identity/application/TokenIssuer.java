package com.awbp.identity.application;

import com.awbp.identity.domain.UserRole;

public interface TokenIssuer {
    String issue(Long userId, UserRole role);
}