package com.awbp.identity.application;

public interface TokenHasher {

    String hash(String rawToken);
}
