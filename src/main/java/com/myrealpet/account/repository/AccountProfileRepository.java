package com.myrealpet.account.repository;

import com.myrealpet.account.entity.AccountProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountProfileRepository extends JpaRepository<AccountProfile, Long> {
    
    Optional<AccountProfile> findByAccountId(Long accountId);
    
    Optional<AccountProfile> findByNickname(String nickname);
    
    boolean existsByNickname(String nickname);
    
    @Query("SELECT ap FROM AccountProfile ap WHERE ap.nickname LIKE %:keyword%")
    List<AccountProfile> findByNicknameContaining(@Param("keyword") String keyword);
    
    @Query("SELECT ap FROM AccountProfile ap JOIN FETCH ap.account WHERE ap.nickname = :nickname")
    Optional<AccountProfile> findByNicknameWithAccount(@Param("nickname") String nickname);
}