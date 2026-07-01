package com.awbp.security;

import com.awbp.common.security.TokenPair;

public record TokenResponse(String accessToken, String refreshToken, String type) {
    public static TokenResponse of(TokenPair pair) {
        return new TokenResponse(pair.accessToken(), pair.refreshToken(), "Bearer");
    }
}