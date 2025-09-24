package com.myrealpet.account.service;

import com.myrealpet.account.dto.LoginResponse;
import com.myrealpet.account.dto.RegisterRequest;
import com.myrealpet.account.entity.Account;
import com.myrealpet.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import com.myrealpet.account.util.PasswordEncoder;
import com.myrealpet.account.util.PhoneNumberFormatter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final PhoneNumberFormatter phoneNumberFormatter;

    @Override
    @Transactional
    public Account createAccount(String username, String password) {
        if (accountRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        Account account = Account.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .provider(Account.AuthProvider.LOCAL)
                .role(Account.Role.USER)
                .isActive(true)
                .build();

        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account createSocialAccount(String username, Account.AuthProvider provider, String providerId) {
        Optional<Account> existingAccount = accountRepository.findByProviderAndProviderId(provider, providerId);
        if (existingAccount.isPresent()) {
            throw new IllegalArgumentException("Social account already exists");
        }

        if (accountRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        Account account = Account.builder()
                .username(username)
                .provider(provider)
                .providerId(providerId)
                .role(Account.Role.USER)
                .isActive(true)
                .build();

        return accountRepository.save(account);
    }

    @Override
    public Optional<Account> findAccountById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public Optional<Account> findAccountByUsername(String username) {
        return accountRepository.findByUsername(username);
    }


    @Override
    public Optional<Account> findAccountByProvider(Account.AuthProvider provider, String providerId) {
        return accountRepository.findByProviderAndProviderId(provider, providerId);
    }

    @Override
    public List<Account> findAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public List<Account> findInactiveAccounts() {
        return accountRepository.findInactiveAccounts();
    }

    @Override
    @Transactional
    public Account updatePassword(Long accountId, String newPassword) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        account.updatePassword(passwordEncoder.encode(newPassword));
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account deactivateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        account.deactivate();
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account activateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        account.activate();
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public void deleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        accountRepository.delete(account);
    }

    @Override
    public boolean isUsernameExists(String username) {
        return accountRepository.existsByUsername(username);
    }

    @Override
    public LoginResponse login(String username, String password) {
        Account account = accountRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or account is deactivated"));

        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        String token = tokenService.generateToken(account.getId());
        return LoginResponse.of(token, account.getId(), account.getUsername(), tokenService.getTokenExpiration());
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest registerRequest) {
        if (accountRepository.existsByUsername(registerRequest.getId())) {
            throw new IllegalArgumentException("Username already exists: " + registerRequest.getId());
        }

        String formattedPhoneNumber = phoneNumberFormatter.formatPhoneNumber(registerRequest.getPhoneNumber());

        Account account = Account.builder()
                .username(registerRequest.getId())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .name(registerRequest.getName())
                .phoneNumber(formattedPhoneNumber)
                .provider(Account.AuthProvider.LOCAL)
                .role(Account.Role.USER)
                .isActive(true)
                .build();

        Account savedAccount = accountRepository.save(account);
        String token = tokenService.generateToken(savedAccount.getId());
        return LoginResponse.of(token, savedAccount.getId(), savedAccount.getUsername(), tokenService.getTokenExpiration());
    }

    @Override
    public void logout(String token) {
        tokenService.invalidateToken(token);
    }

    @Override
    public void logoutAll(Long accountId) {
        tokenService.invalidateAllUserTokens(accountId);
    }

    @Override
    @Transactional
    public LoginResponse loginWithKakaoToken(String kakaoAccessToken) {
        try {
            // 카카오 API를 호출하여 사용자 정보 가져오기
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + kakaoAccessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                Map.class
            );

            Map<String, Object> kakaoUser = response.getBody();
            if (kakaoUser == null) {
                throw new IllegalArgumentException("카카오 사용자 정보를 가져올 수 없습니다.");
            }

            String providerId = kakaoUser.get("id").toString();
            Map<String, Object> properties = (Map<String, Object>) kakaoUser.get("properties");
            String nickname = properties != null ? (String) properties.get("nickname") : "카카오사용자";

            // 기존 계정 확인 또는 새 계정 생성
            Optional<Account> existingAccount = accountRepository.findByProviderAndProviderId(
                Account.AuthProvider.KAKAO, providerId);

            Account account;
            if (existingAccount.isPresent()) {
                account = existingAccount.get();
                // 계정이 비활성화된 경우 활성화
                if (!account.getIsActive()) {
                    account = activateAccount(account.getId());
                }
            } else {
                // 새 소셜 계정 생성
                account = Account.builder()
                    .username(nickname + "_" + providerId) // 고유한 username 생성
                    .provider(Account.AuthProvider.KAKAO)
                    .providerId(providerId)
                    .role(Account.Role.USER)
                    .isActive(true)
                    .build();
                account = accountRepository.save(account);
            }

            // JWT 토큰 생성
            String accessJwtToken = tokenService.generateAccessToken(account.getId());
            String refreshJwtToken = tokenService.generateRefreshToken(account.getId());

            return LoginResponse.builder()
                .token(accessJwtToken)
                .refreshToken(refreshJwtToken)
                .accountId(account.getId())
                .username(account.getUsername())
                .expiresInSeconds(3600L) // 1시간
                .build();

        } catch (Exception e) {
            throw new IllegalArgumentException("카카오 로그인 처리 실패: " + e.getMessage());
        }
    }

    @Override
    public Account getCurrentUser(String token) {
        try {
            Long accountId = tokenService.validateToken(token);
            return findAccountById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        } catch (Exception e) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }
    }

}