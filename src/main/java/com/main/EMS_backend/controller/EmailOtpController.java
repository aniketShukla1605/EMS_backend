package com.main.EMS_backend.controller;

import com.main.EMS_backend.dto.OtpRequest;
import com.main.EMS_backend.entity.EmailOtp;
import com.main.EMS_backend.entity.User;
import com.main.EMS_backend.repository.EmailOtpRepository;
import com.main.EMS_backend.repository.UserRepository;
import com.main.EMS_backend.service.EmailOtpService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/otp")
@Slf4j
public class EmailOtpController {
    @Autowired
    private EmailOtpRepository emailOtpRepository;
    @Autowired
    private EmailOtpService emailOtpService;
    private UserRepository userRepository;

    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest request) throws MessagingException {
//        String otp = String.valueOf((int)(Math.random()*9000+1000));
        String email = request.getEmail();


        SecureRandom random = new SecureRandom();
        int otpInt = 100000 + random.nextInt(900000);
        String otp = String.valueOf(otpInt);
        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setEmail(email);
        emailOtp.setOtp(otp);
        emailOtp.setDateTime(LocalDateTime.now().plusMinutes(5));

        emailOtpRepository.save(emailOtp);

        emailOtpService.sendOtp(email,otp);
        return ResponseEntity.ok("OTP sent");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest otpRequest) throws MessagingException {
        Optional<EmailOtp> emailOtp = emailOtpRepository.findByEmailAndOtp(otpRequest.getEmail(),otpRequest.getOtp());
        if(!emailOtp.isPresent()){
            return ResponseEntity.badRequest().body("Invalid OTP");
        }
        if(emailOtp.get().getDateTime().isBefore(LocalDateTime.now())){
            return ResponseEntity.badRequest().body("OTP Expired");
        }

        return ResponseEntity.ok("Email Verified");
    }
}
