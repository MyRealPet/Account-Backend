package com.myrealpet.account.oauth;

import com.myrealpet.account.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenService tokenService;

    @Value("${cors.allowed-origins:http://localhost:5173}")
    private String[] allowedOrigins;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String accessToken = tokenService.generateAccessToken(oAuth2User.getUserId());
        String refreshToken = tokenService.generateRefreshToken(oAuth2User.getUserId());

        // 프론트엔드 URL 동적 결정 (요청의 origin 사용 또는 기본값)
        String frontendUrl = request.getHeader("Referer");
        if (frontendUrl == null || frontendUrl.isEmpty()) {
            frontendUrl = allowedOrigins[0]; // 기본값 사용
        } else {
            // Referer에서 origin 부분만 추출
            try {
                java.net.URL url = new java.net.URL(frontendUrl);
                frontendUrl = url.getProtocol() + "://" + url.getHost() +
                    (url.getPort() != -1 ? ":" + url.getPort() : "");
            } catch (Exception e) {
                frontendUrl = allowedOrigins[0]; // 파싱 실패시 기본값 사용
            }
        }

        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                .queryParam("token", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}