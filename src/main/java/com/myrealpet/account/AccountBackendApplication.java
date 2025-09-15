package com.myrealpet.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AccountBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountBackendApplication.class, args);
    }

}