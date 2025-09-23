package com.myrealpet.account.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PhoneNumberFormatter {

    private static final Pattern PHONE_DIGITS_ONLY = Pattern.compile("\\D");
    private static final Pattern PHONE_VALIDATION = Pattern.compile("^01[016789]\\d{7,8}$");

    public String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return null;
        }

        String digitsOnly = removeNonDigits(phoneNumber);

        if (!isValidPhoneNumber(digitsOnly)) {
            throw new IllegalArgumentException("잘못된 핸드폰 번호 형식입니다: " + phoneNumber);
        }

        return addHyphens(digitsOnly);
    }

    public String removeNonDigits(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        return PHONE_DIGITS_ONLY.matcher(phoneNumber).replaceAll("");
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }

        String digitsOnly = removeNonDigits(phoneNumber);
        return PHONE_VALIDATION.matcher(digitsOnly).matches();
    }

    private String addHyphens(String digitsOnly) {
        if (digitsOnly.length() == 10) {
            // 010-123-4567 형태 (구 번호체계)
            return String.format("%s-%s-%s",
                digitsOnly.substring(0, 3),
                digitsOnly.substring(3, 6),
                digitsOnly.substring(6)
            );
        } else if (digitsOnly.length() == 11) {
            // 010-1234-5678 형태 (신 번호체계)
            return String.format("%s-%s-%s",
                digitsOnly.substring(0, 3),
                digitsOnly.substring(3, 7),
                digitsOnly.substring(7)
            );
        }

        throw new IllegalArgumentException("핸드폰 번호는 10자리 또는 11자리여야 합니다.");
    }

    public boolean isAlreadyFormatted(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        return phoneNumber.matches("^010-\\d{3,4}-\\d{4}$");
    }
}