package com.main.EMS_backend.controller;

import com.main.EMS_backend.dto.OtpRequest;
import com.main.EMS_backend.service.OtpService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@Slf4j
public class EmailOtpController {
    private final OtpService otpService;

    public EmailOtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest request) throws MessagingException {

        try {
            otpService.generateAndSendOtp(request.getEmail());
        } catch (Exception e) {
            log.error("Failed to send OTP to {}", request.getEmail(), e);
            return ResponseEntity.status(500).body("Failed to send otp");
        }

        return ResponseEntity.ok("OTP sent");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest otpRequest) throws MessagingException {
        if(!otpService.verifyOtp(otpRequest.getEmail(),otpRequest.getOtp())) {
            return ResponseEntity.status(401).body("Wrong Otp");
        }
        return ResponseEntity.ok("Email Verified");
    }
}
