package com.myrealpet.account.controller;

import com.myrealpet.account.util.PhoneNumberFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class PhoneTestController {

    private final PhoneNumberFormatter phoneNumberFormatter;

    @PostMapping("/format-phone")
    public ResponseEntity<String> formatPhone(@RequestParam String phoneNumber) {
        try {
            String formatted = phoneNumberFormatter.formatPhoneNumber(phoneNumber);
            return ResponseEntity.ok(formatted);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("오류: " + e.getMessage());
        }
    }

    @GetMapping("/validate-phone")
    public ResponseEntity<Boolean> validatePhone(@RequestParam String phoneNumber) {
        boolean isValid = phoneNumberFormatter.isValidPhoneNumber(phoneNumber);
        return ResponseEntity.ok(isValid);
    }
}