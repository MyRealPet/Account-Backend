package com.myrealpet.account.service;

import com.myrealpet.account.entity.Account;
import com.myrealpet.account.entity.AccountProfile;
import com.myrealpet.account.repository.AccountRepository;
import com.myrealpet.account.repository.AccountProfileRepository;
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
    private final AccountProfileRepository accountProfileRepository;
    private final PasswordEncoder passwordEncoder ;

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
    public Optional<Account> findAccountWithProfile(String username) {
        return accountRepository.findByUsernameWithProfile(username);
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
    @Transactional
    public AccountProfile createProfile(Long accountId, String nickname) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        if (accountProfileRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("Nickname already exists: " + nickname);
        }

        AccountProfile profile = AccountProfile.builder()
                .account(account)
                .nickname(nickname)
                .build();

        return accountProfileRepository.save(profile);
    }

    @Override
    public Optional<AccountProfile> findProfileByAccountId(Long accountId) {
        return accountProfileRepository.findByAccountId(accountId);
    }

    @Override
    public Optional<AccountProfile> findProfileByNickname(String nickname) {
        return accountProfileRepository.findByNickname(nickname);
    }

    @Override
    @Transactional
    public AccountProfile updateProfile(Long accountId, String nickname, String profileImageUrl,
                                       String phone, String bio) {
        AccountProfile profile = accountProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for account: " + accountId));

        if (nickname != null && !nickname.equals(profile.getNickname())) {
            if (accountProfileRepository.existsByNickname(nickname)) {
                throw new IllegalArgumentException("Nickname already exists: " + nickname);
            }
            profile.updateNickname(nickname);
        }

        if (profileImageUrl != null) {
            profile.updateProfileImage(profileImageUrl);
        }

        if (phone != null) {
            profile.updatePhone(phone);
        }

        if (bio != null) {
            profile.updateBio(bio);
        }

        return accountProfileRepository.save(profile);
    }

    @Override
    public List<AccountProfile> searchProfilesByNickname(String keyword) {
        return accountProfileRepository.findByNicknameContaining(keyword);
    }

    @Override
    public boolean isNicknameExists(String nickname) {
        return accountProfileRepository.existsByNickname(nickname);
    }
}