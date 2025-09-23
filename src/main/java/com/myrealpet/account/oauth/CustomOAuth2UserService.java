package com.myrealpet.account.oauth;

import com.myrealpet.account.entity.Account;
import com.myrealpet.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AccountRepository accountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Account account = saveOrUpdate(attributes);

        return new CustomOAuth2User(account, oAuth2User.getAttributes(), userNameAttributeName);
    }

    private Account saveOrUpdate(OAuthAttributes attributes) {
        Account account = accountRepository.findByProviderAndProviderId(
                attributes.getProvider(),
                attributes.getProviderId()
        ).orElse(null);

        if (account == null) {
            account = Account.builder()
                    .username(attributes.getEmail())
                    .name(attributes.getName())
                    .provider(attributes.getProvider())
                    .providerId(attributes.getProviderId())
                    .role(Account.Role.USER)
                    .isActive(true)
                    .build();
        }

        return accountRepository.save(account);
    }
}