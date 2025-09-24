package com.myrealpet.account.config;

import com.myrealpet.account.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");

        log.debug("JWT Filter - Path: {}, Authorization Header: {}", requestPath, authHeader != null ? "Present" : "Not Present");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.debug("JWT Filter - Extracted token: {}", token.substring(0, Math.min(token.length(), 10)) + "...");

            try {
                Long accountId = tokenService.validateToken(token);
                if (accountId != null) {
                    log.debug("JWT Filter - Token valid for account ID: {}", accountId);
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            accountId,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("JWT Filter - Authentication set in SecurityContext");
                } else {
                    log.debug("JWT Filter - Token validation returned null");
                }
            } catch (Exception e) {
                log.debug("JWT Filter - Token validation failed: {}", e.getMessage());
            }
        } else {
            log.debug("JWT Filter - No valid Authorization header found");
        }

        filterChain.doFilter(request, response);
    }
}