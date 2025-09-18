package com.myrealpet.account_profile.controller;

import com.myrealpet.account_profile.entity.AccountProfile;
import com.myrealpet.account_profile.service.AccountProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class AccountProfileController {

    private final AccountProfileService accountProfileService;

    @PostMapping
    public ResponseEntity<AccountProfile> createProfile(@RequestParam Long accountId,
                                                       @RequestParam String nickname) {
        AccountProfile profile = accountProfileService.createProfile(accountId, nickname);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<AccountProfile> getProfileByAccountId(@PathVariable Long accountId) {
        Optional<AccountProfile> profile = accountProfileService.findProfileByAccountId(accountId);
        return profile.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<AccountProfile> getProfileByNickname(@PathVariable String nickname) {
        Optional<AccountProfile> profile = accountProfileService.findProfileByNickname(nickname);
        return profile.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/account/{accountId}")
    public ResponseEntity<AccountProfile> updateProfile(@PathVariable Long accountId,
                                                       @RequestParam(required = false) String nickname,
                                                       @RequestParam(required = false) String profileImageUrl,
                                                       @RequestParam(required = false) String phone,
                                                       @RequestParam(required = false) String bio) {
        AccountProfile profile = accountProfileService.updateProfile(accountId, nickname, profileImageUrl, phone, bio);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/account/{accountId}/nickname")
    public ResponseEntity<AccountProfile> updateNickname(@PathVariable Long accountId,
                                                        @RequestParam String nickname) {
        AccountProfile profile = accountProfileService.updateNickname(accountId, nickname);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/account/{accountId}/profile-image")
    public ResponseEntity<AccountProfile> updateProfileImage(@PathVariable Long accountId,
                                                            @RequestParam String profileImageUrl) {
        AccountProfile profile = accountProfileService.updateProfileImage(accountId, profileImageUrl);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/account/{accountId}/phone")
    public ResponseEntity<AccountProfile> updatePhone(@PathVariable Long accountId,
                                                     @RequestParam String phone) {
        AccountProfile profile = accountProfileService.updatePhone(accountId, phone);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/account/{accountId}/birth-date")
    public ResponseEntity<AccountProfile> updateBirthDate(@PathVariable Long accountId,
                                                         @RequestParam LocalDate birthDate) {
        AccountProfile profile = accountProfileService.updateBirthDate(accountId, birthDate);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/account/{accountId}/gender")
    public ResponseEntity<AccountProfile> updateGender(@PathVariable Long accountId,
                                                      @RequestParam AccountProfile.Gender gender) {
        AccountProfile profile = accountProfileService.updateGender(accountId, gender);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/account/{accountId}/bio")
    public ResponseEntity<AccountProfile> updateBio(@PathVariable Long accountId,
                                                   @RequestParam String bio) {
        AccountProfile profile = accountProfileService.updateBio(accountId, bio);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AccountProfile>> searchProfiles(@RequestParam String keyword) {
        List<AccountProfile> profiles = accountProfileService.searchProfilesByNickname(keyword);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/check-nickname/{nickname}")
    public ResponseEntity<Boolean> checkNicknameExists(@PathVariable String nickname) {
        boolean exists = accountProfileService.isNicknameExists(nickname);
        return ResponseEntity.ok(exists);
    }

    @DeleteMapping("/account/{accountId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long accountId) {
        accountProfileService.deleteProfile(accountId);
        return ResponseEntity.noContent().build();
    }
}