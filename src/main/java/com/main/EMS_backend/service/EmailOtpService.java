package com.main.EMS_backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailOtpService {
    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailOtpService(JavaMailSender mailSender,
                           @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void sendOtp(String email, String otp) throws MessagingException {
        if (fromAddress == null || fromAddress.isBlank()) {
            throw new MessagingException("Mail username is not configured");
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        String html = """
                <html>
                  <body style="font-family: Arial; background:#0f172a; color:white; padding:20px;">
                    <div style="max-width:500px; margin:auto; background:#1e293b; padding:20px; border-radius:10px;">
                
                      <h2 style="color:#38bdf8;">EventSphere</h2>
                
                      <p>Hello,</p>
                
                      <p>Your OTP for verification is:</p>
                
                      <h1 style="background:#38bdf8; color:black; padding:10px; text-align:center; border-radius:8px;">
                """ + otp + """
                      </h1>
                
                      <p>This OTP is valid for 5 minutes.</p>
                
                      <p style="font-size:12px; color:gray;">
                        If you didn't request this, ignore this email.
                      </p>
                
                    </div>
                  </body>
                </html>
                """;

        helper.setFrom(fromAddress);
        helper.setTo(email);
        helper.setSubject("Email verification OTP");
        helper.setText(html, true);
        mailSender.send(mimeMessage);
    }
}
