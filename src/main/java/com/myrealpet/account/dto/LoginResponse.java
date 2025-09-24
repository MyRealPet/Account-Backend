package com.myrealpet.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String refreshToken;
    private Long accountId;
    private String username;
    private long expiresInSeconds;

    public static LoginResponse of(String token, Long accountId, String username, Duration expiration) {
        return new LoginResponse(token, null, accountId, username, expiration.toSeconds());
    }
}