package com.myrealpet.account.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class OAuthController {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @GetMapping("/kakao/url")
    public ResponseEntity<Map<String, String>> getKakaoAuthUrl() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + kakaoClientId +
                "&redirect_uri=" + kakaoRedirectUri +
                "&response_type=code" +
                "&scope=profile_nickname,account_email";

        Map<String, String> response = new HashMap<>();
        response.put("authUrl", kakaoAuthUrl);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/callback/success")
    public ResponseEntity<Map<String, String>> oauthCallbackSuccess(
            @RequestParam String accessToken,
            @RequestParam String refreshToken) {

        Map<String, String> response = new HashMap<>();
        response.put("message", "카카오 로그인 성공");
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);

        return ResponseEntity.ok(response);
    }
}