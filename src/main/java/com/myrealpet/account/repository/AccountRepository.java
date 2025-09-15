package com.myrealpet.account.repository;

import com.myrealpet.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    Optional<Account> findByProviderAndProviderId(Account.AuthProvider provider, String providerId);
    
    Optional<Account> findByUsernameAndIsActiveTrue(String username);
    
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.profile WHERE a.username = :username")
    Optional<Account> findByUsernameWithProfile(@Param("username") String username);
    
    @Query("SELECT a FROM Account a WHERE a.isActive = false")
    List<Account> findInactiveAccounts();
}