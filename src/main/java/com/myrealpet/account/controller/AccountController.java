package com.myrealpet.account.controller;

import com.myrealpet.account.entity.Account;
import com.myrealpet.account.entity.AccountProfile;
import com.myrealpet.account.redis_cache.RedisCacheService;
import com.myrealpet.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final RedisCacheService redisCacheService;
    private final AccountService accountService;


    @PostMapping("/register")
    public ResponseEntity<Account> createAccount(@RequestParam String username,
                                               @RequestParam String password) {
        Account account = accountService.createAccount(username, password);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @PostMapping("/social-register")
    public ResponseEntity<Account> createSocialAccount(@RequestParam String username,
                                                     @RequestParam Account.AuthProvider provider,
                                                     @RequestParam String providerId) {
        Account account = accountService.createSocialAccount(username, provider, providerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Long id) {
        Optional<Account> account = accountService.findAccountById(id);
        return account.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Account> getAccountByUsername(@PathVariable String username) {
        Optional<Account> account = accountService.findAccountByUsername(username);
        return account.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}/with-profile")
    public ResponseEntity<Account> getAccountWithProfile(@PathVariable String username) {
        Optional<Account> account = accountService.findAccountWithProfile(username);
        return account.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.findAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<Account>> getInactiveAccounts() {
        List<Account> accounts = accountService.findInactiveAccounts();
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Account> updatePassword(@PathVariable Long id,
                                                @RequestParam String newPassword) {
        Account account = accountService.updatePassword(id, newPassword);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Account> deactivateAccount(@PathVariable Long id) {
        Account account = accountService.deactivateAccount(id);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Account> activateAccount(@PathVariable Long id) {
        Account account = accountService.activateAccount(id);
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        boolean exists = accountService.isUsernameExists(username);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/{accountId}/profile")
    public ResponseEntity<AccountProfile> createProfile(@PathVariable Long accountId,
                                                       @RequestParam String nickname) {
        AccountProfile profile = accountService.createProfile(accountId, nickname);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @GetMapping("/{accountId}/profile")
    public ResponseEntity<AccountProfile> getProfile(@PathVariable Long accountId) {
        Optional<AccountProfile> profile = accountService.findProfileByAccountId(accountId);
        return profile.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profile/nickname/{nickname}")
    public ResponseEntity<AccountProfile> getProfileByNickname(@PathVariable String nickname) {
        Optional<AccountProfile> profile = accountService.findProfileByNickname(nickname);
        return profile.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{accountId}/profile")
    public ResponseEntity<AccountProfile> updateProfile(@PathVariable Long accountId,
                                                       @RequestParam(required = false) String nickname,
                                                       @RequestParam(required = false) String profileImageUrl,
                                                       @RequestParam(required = false) String phone,
                                                       @RequestParam(required = false) String bio) {
        AccountProfile profile = accountService.updateProfile(accountId, nickname, profileImageUrl, phone, bio);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/profile/search")
    public ResponseEntity<List<AccountProfile>> searchProfiles(@RequestParam String keyword) {
        List<AccountProfile> profiles = accountService.searchProfilesByNickname(keyword);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/check-nickname/{nickname}")
    public ResponseEntity<Boolean> checkNicknameExists(@PathVariable String nickname) {
        boolean exists = accountService.isNicknameExists(nickname);
        return ResponseEntity.ok(exists);
    }
}