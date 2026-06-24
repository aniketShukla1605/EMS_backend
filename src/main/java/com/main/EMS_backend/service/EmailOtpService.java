package com.main.EMS_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class EmailOtpService {
    @Value("${brevo.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOtp(String email, String otp) {
        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        String body = """
            {
              "sender": {"name": "EventSphere", "email": "your-verified-sender@gmail.com"},
              "to": [{"email": "%s"}],
              "subject": "Email Verification OTP",
              "htmlContent": "<h1>Your OTP is: %s</h1><p>Valid for 5 minutes.</p>"
            }
            """.formatted(email, otp);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, request, String.class);
    }
}