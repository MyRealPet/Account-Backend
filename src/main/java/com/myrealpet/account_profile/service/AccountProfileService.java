package com.myrealpet.account_profile.service;

import com.myrealpet.account_profile.entity.AccountProfile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AccountProfileService {

    AccountProfile createProfile(Long accountId, String nickname);

    Optional<AccountProfile> findProfileByAccountId(Long accountId);

    Optional<AccountProfile> findProfileByNickname(String nickname);

    AccountProfile updateProfile(Long accountId, String nickname, String profileImageUrl,
                                String phone, String bio);

    AccountProfile updateNickname(Long accountId, String nickname);

    AccountProfile updateProfileImage(Long accountId, String profileImageUrl);

    AccountProfile updatePhone(Long accountId, String phone);

    AccountProfile updateBirthDate(Long accountId, LocalDate birthDate);

    AccountProfile updateGender(Long accountId, AccountProfile.Gender gender);

    AccountProfile updateBio(Long accountId, String bio);

    List<AccountProfile> searchProfilesByNickname(String keyword);

    boolean isNicknameExists(String nickname);

    void deleteProfile(Long accountId);
}