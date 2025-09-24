package com.myrealpet.account.service;

import java.time.Duration;

public interface TokenService {

    String generateToken(Long accountId);

    String generateAccessToken(Long accountId);

    String generateRefreshToken(Long accountId);

    Long validateToken(String token);

    void invalidateToken(String token);

    void invalidateAllUserTokens(Long accountId);

    boolean isTokenValid(String token);

    Duration getTokenExpiration();
}