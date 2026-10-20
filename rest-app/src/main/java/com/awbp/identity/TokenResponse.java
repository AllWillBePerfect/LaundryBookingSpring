package com.awbp.identity;

import com.awbp.identity.domain.TokenPair;

public record TokenResponse(String accessToken, String refreshToken, String type) {
    public static TokenResponse of(TokenPair pair) {
        return new TokenResponse(pair.accessToken(), pair.refreshToken(), "Bearer");
    }
}