package com.myrealpet.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.myrealpet.account", "com.myrealpet.account_profile"})
@EntityScan(basePackages = {"com.myrealpet.account.entity", "com.myrealpet.account_profile.entity"})
@EnableJpaRepositories(basePackages = {"com.myrealpet.account.repository", "com.myrealpet.account_profile.repository"})
@EnableJpaAuditing
public class AccountBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountBackendApplication.class, args);
    }

}