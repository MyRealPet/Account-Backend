package com.myrealpet.account.util;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class PasswordEncoder {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    public String encode(String password) {
        try {
            String salt = generateSalt();
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());

            String encodedHash = Base64.getEncoder().encodeToString(hashedPassword);
            return salt + ":" + encodedHash;
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode password", e);
        }
    }

    public boolean matches(String password, String encodedPassword) {
        try {
            String[] parts = encodedPassword.split(":");
            if (parts.length != 2) {
                return false;
            }

            String salt = parts[0];
            String storedHash = parts[1];

            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());

            String encodedHash = Base64.getEncoder().encodeToString(hashedPassword);
            return storedHash.equals(encodedHash);
        } catch (Exception e) {
            return false;
        }
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}