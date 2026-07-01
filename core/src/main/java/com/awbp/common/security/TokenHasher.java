package com.awbp.common.security;

public interface TokenHasher {

    String hash(String rawToken);
}
