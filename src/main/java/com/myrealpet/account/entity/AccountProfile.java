package com.myrealpet.account.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_profile")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AccountProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @Column(unique = true, length = 20)
    private String nickname;
    
    @Column(length = 500)
    private String profileImageUrl;
    
    @Column(length = 15)
    private String phone;
    
    private LocalDate birthDate;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Column(length = 500)
    private String bio;
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum Gender {
        MALE, FEMALE, OTHER
    }
    
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    
    public void updatePhone(String phone) {
        this.phone = phone;
    }
    
    public void updateBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    public void updateGender(Gender gender) {
        this.gender = gender;
    }
    
    public void updateBio(String bio) {
        this.bio = bio;
    }
}