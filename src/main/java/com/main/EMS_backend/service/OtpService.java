package com.main.EMS_backend.service;

import com.main.EMS_backend.entity.EmailOtp;
import com.main.EMS_backend.repository.EmailOtpRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

public class OtpService {
    private final EmailOtpRepository otpRepository;
    private final EmailService emailService;

    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(EmailOtpRepository otpRepository, EmailService emailService) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    public void generateAndSendOtp(String email) {
        otpRepository.deleteByEmail(email);

        String otp = String.format("%06d", secureRandom.nextInt(999999));

        EmailOtp otpDetails = new EmailOtp(email, otp, LocalDateTime.now());
        otpRepository.save(otpDetails);

        try {
            emailService.sendOtpEmail(email, otp);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean verifyOtp(String email, String userProvidedOtp) {
        Optional<EmailOtp> otpOpt = otpRepository.findByEmail(email);

        if (otpOpt.isPresent()) {
            EmailOtp storedOtp = otpOpt.get();

            if (storedOtp.getOtp().equals(userProvidedOtp)) {
                otpRepository.deleteByEmail(email);
                return true;
            }
        }

        return false;
    }

}
