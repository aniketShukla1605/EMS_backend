package com.main.EMS_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class EmailService {
    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.mail}")
    private String senderEmail;

    public void sendOtpEmail(String toEmail, String otp) {

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);
        headers.set("accept", "application/json");

        String htmlContent = """
                <html>
                  <body style="font-family: Arial; background:#0f172a; color:white; padding:20px;">
                    <div style="max-width:500px; margin:auto; background:#1e293b; padding:20px; border-radius:10px;">
                
                      <h2 style="color:#38bdf8;">EventSphere</h2>
                
                      <p>Hello,</p>
                
                      <p>Your OTP for verification is below:</p>
                
                      <h1 style="background:#38bdf8; color:black; padding:10px; text-align:center; border-radius:8px;">
     
                      </h1>
                
                      <p>This OTP is valid for 5 minutes.</p>
                
                      <p style="font-size:12px; color:gray;">
                        If you didn't request this, ignore this email.
                      </p>
                
                    </div>
                  </body>
                </html>
                """.formatted(otp);


        Map<String, Object> body = Map.of(
                "sender", Map.of("name", "EventSphere", "email", senderEmail),
                "to", List.of(Map.of("email", toEmail)),
                "subject", "EventSphere Email Verification OTP",
                "htmlContent", htmlContent
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
            System.out.println("Success! OTP sent to " + toEmail + " via Brevo API");
        } catch (Exception e) {
            System.err.println("Brevo API Failed: " + e.getMessage());
            throw new RuntimeException("Failed to send OTP email via API");
        }
    }
}
