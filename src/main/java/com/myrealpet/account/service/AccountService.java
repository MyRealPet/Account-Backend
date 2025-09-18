package com.myrealpet.account.service;

import com.myrealpet.account.entity.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    Account createAccount(String username, String password);

    Account createSocialAccount(String username, Account.AuthProvider provider, String providerId);

    Optional<Account> findAccountById(Long id);

    Optional<Account> findAccountByUsername(String username);

    Optional<Account> findAccountWithProfile(String username);

    Optional<Account> findAccountByProvider(Account.AuthProvider provider, String providerId);

    List<Account> findAllAccounts();

    List<Account> findInactiveAccounts();

    Account updatePassword(Long accountId, String newPassword);

    Account deactivateAccount(Long accountId);

    Account activateAccount(Long accountId);

    void deleteAccount(Long accountId);

    boolean isUsernameExists(String username);

}
