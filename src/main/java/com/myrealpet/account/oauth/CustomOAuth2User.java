package com.myrealpet.account.oauth;

import com.myrealpet.account.entity.Account;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final Account account;
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()));
    }

    @Override
    public String getName() {
        return attributes.get(nameAttributeKey).toString();
    }

    public Long getUserId() {
        return account.getId();
    }

    public String getUsername() {
        return account.getUsername();
    }

    public String getUserName() {
        return account.getName();
    }
}