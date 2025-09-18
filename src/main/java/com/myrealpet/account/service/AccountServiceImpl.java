package com.myrealpet.account.service;

import com.myrealpet.account.dto.LoginResponse;
import com.myrealpet.account.dto.RegisterRequest;
import com.myrealpet.account.entity.Account;
import com.myrealpet.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import com.myrealpet.account.util.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

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

        Account account = Account.builder()
                .username(registerRequest.getId())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .name(registerRequest.getName())
                .phoneNumber(registerRequest.getPhoneNumber())
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

}