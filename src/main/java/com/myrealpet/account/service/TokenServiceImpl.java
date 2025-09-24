package com.myrealpet.account.service;

import com.myrealpet.account.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RedisCacheService redisCacheService;

    private static final String TOKEN_PREFIX = "auth_token:";
    private static final String ACCESS_TOKEN_PREFIX = "access_token:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String USER_TOKEN_PREFIX = "user_tokens:";
    private static final Duration TOKEN_EXPIRATION = Duration.ofHours(24);
    private static final Duration ACCESS_TOKEN_EXPIRATION = Duration.ofHours(1);
    private static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(7);

    @Override
    public String generateToken(Long accountId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String tokenKey = TOKEN_PREFIX + token;
        String userTokenKey = USER_TOKEN_PREFIX + accountId;

        redisCacheService.setValueWithExpiration(tokenKey, accountId.toString(), TOKEN_EXPIRATION);

        redisCacheService.addToSet(userTokenKey, token);
        redisCacheService.setExpiration(userTokenKey, TOKEN_EXPIRATION);

        log.info("Generated token for account ID: {}", accountId);
        return token;
    }

    @Override
    public String generateAccessToken(Long accountId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String tokenKey = ACCESS_TOKEN_PREFIX + token;
        String userTokenKey = USER_TOKEN_PREFIX + accountId;

        redisCacheService.setValueWithExpiration(tokenKey, accountId.toString(), ACCESS_TOKEN_EXPIRATION);

        redisCacheService.addToSet(userTokenKey, token);
        redisCacheService.setExpiration(userTokenKey, ACCESS_TOKEN_EXPIRATION);

        log.info("Generated access token for account ID: {}", accountId);
        return token;
    }

    @Override
    public String generateRefreshToken(Long accountId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String tokenKey = REFRESH_TOKEN_PREFIX + token;
        String userTokenKey = USER_TOKEN_PREFIX + accountId + ":refresh";

        redisCacheService.setValueWithExpiration(tokenKey, accountId.toString(), REFRESH_TOKEN_EXPIRATION);

        redisCacheService.addToSet(userTokenKey, token);
        redisCacheService.setExpiration(userTokenKey, REFRESH_TOKEN_EXPIRATION);

        log.info("Generated refresh token for account ID: {}", accountId);
        return token;
    }

    @Override
    public Long validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        // Try access token first
        String accessTokenKey = ACCESS_TOKEN_PREFIX + token;
        String accountIdStr = redisCacheService.getValue(accessTokenKey);

        if (accountIdStr != null) {
            try {
                return Long.parseLong(accountIdStr);
            } catch (NumberFormatException e) {
                log.warn("Invalid account ID format in access token: {}", token);
                invalidateToken(token);
                return null;
            }
        }

        // Try regular token
        String tokenKey = TOKEN_PREFIX + token;
        accountIdStr = redisCacheService.getValue(tokenKey);

        if (accountIdStr != null) {
            try {
                return Long.parseLong(accountIdStr);
            } catch (NumberFormatException e) {
                log.warn("Invalid account ID format in token: {}", token);
                invalidateToken(token);
                return null;
            }
        }

        return null;
    }

    @Override
    public void invalidateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return;
        }

        String tokenKey = TOKEN_PREFIX + token;
        String accountIdStr = redisCacheService.getValue(tokenKey);

        if (accountIdStr != null) {
            try {
                Long accountId = Long.parseLong(accountIdStr);
                String userTokenKey = USER_TOKEN_PREFIX + accountId;
                redisCacheService.removeFromSet(userTokenKey, token);
            } catch (NumberFormatException e) {
                log.warn("Invalid account ID format when invalidating token: {}", token);
            }
        }

        redisCacheService.deleteValue(tokenKey);
        log.info("Invalidated token: {}", token);
    }

    @Override
    public void invalidateAllUserTokens(Long accountId) {
        String userTokenKey = USER_TOKEN_PREFIX + accountId;

        redisCacheService.getSetMembers(userTokenKey).forEach(token -> {
            String tokenKey = TOKEN_PREFIX + token;
            redisCacheService.deleteValue(tokenKey);
        });

        redisCacheService.deleteValue(userTokenKey);
        log.info("Invalidated all tokens for account ID: {}", accountId);
    }

    @Override
    public boolean isTokenValid(String token) {
        return validateToken(token) != null;
    }

    @Override
    public Duration getTokenExpiration() {
        return TOKEN_EXPIRATION;
    }
}