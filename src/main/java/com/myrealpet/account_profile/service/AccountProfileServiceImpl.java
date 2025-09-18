package com.myrealpet.account_profile.service;

import com.myrealpet.account.entity.Account;
import com.myrealpet.account.repository.AccountRepository;
import com.myrealpet.account_profile.entity.AccountProfile;
import com.myrealpet.account_profile.repository.AccountProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountProfileServiceImpl implements AccountProfileService {

    private final AccountRepository accountRepository;
    private final AccountProfileRepository accountProfileRepository;

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
    @Transactional
    public AccountProfile updateNickname(Long accountId, String nickname) {
        AccountProfile profile = accountProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for account: " + accountId));

        if (accountProfileRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("Nickname already exists: " + nickname);
        }

        profile.updateNickname(nickname);
        return accountProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public AccountProfile updateProfileImage(Long accountId, String profileImageUrl) {
        AccountProfile profile = accountProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for account: " + accountId));

        profile.updateProfileImage(profileImageUrl);
        return accountProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public AccountProfile updatePhone(Long accountId, String phone) {
        AccountProfile profile = accountProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for account: " + accountId));

        profile.updatePhone(phone);
        return accountProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public AccountProfile updateBirthDate(Long accountId, LocalDate birthDate) {
        AccountProfile profile = accountProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for account: " + accountId));

        profile.updateBirthDate(birthDate);
        return accountProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public AccountProfile updateGender(Long accountId, AccountProfile.Gender gender) {
        AccountProfile profile = accountProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for account: " + accountId));

        profile.updateGender(gender);
        return accountProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public AccountProfile updateBio(Long accountId, String bio) {
        AccountProfile profile = accountProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for account: " + accountId));

        profile.updateBio(bio);
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

    @Override
    @Transactional
    public void deleteProfile(Long accountId) {
        AccountProfile profile = accountProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for account: " + accountId));

        accountProfileRepository.delete(profile);
    }
}