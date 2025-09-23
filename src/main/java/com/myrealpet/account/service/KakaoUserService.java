package com.myrealpet.account.service;

import com.myrealpet.account.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoUserService {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://kapi.kakao.com")
            .build();

    @SuppressWarnings("unchecked")
    public Mono<Map<String, Object>> getKakaoUserInfo(String accessToken) {
        return webClient.get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (Map<String, Object>) response);
    }

    public Account convertToAccount(Map<String, Object> kakaoUserInfo) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoUserInfo.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String providerId = String.valueOf(kakaoUserInfo.get("id"));
        String nickname = (String) profile.get("nickname");
        String email = (String) kakaoAccount.get("email");

        return Account.builder()
                .username(email != null ? email : "kakao_" + providerId)
                .name(nickname)
                .provider(Account.AuthProvider.KAKAO)
                .providerId(providerId)
                .role(Account.Role.USER)
                .isActive(true)
                .build();
    }

    public Mono<Void> unlinkKakaoAccount(String accessToken) {
        return webClient.post()
                .uri("/v1/user/unlink")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .bodyToMono(Void.class);
    }
}