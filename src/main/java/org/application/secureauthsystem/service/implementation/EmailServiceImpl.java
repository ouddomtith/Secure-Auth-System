package org.application.secureauthsystem.service.implementation;

import lombok.AllArgsConstructor;
import org.application.secureauthsystem.service.EmailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String toEmail, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("🔐 Your OTP Code - SecureAuthSystem");
            helper.setText(buildEmailBody(otpCode), true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }

    // ── Email Template ───────────────────────────────────
    private String buildEmailBody(String otpCode) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <div style="max-width: 500px; margin: auto; background: #f9f9f9;
                                padding: 30px; border-radius: 10px;">
                        <h2 style="color: #333;">🔐 Your OTP Code</h2>
                        <p style="color: #555;">Use this code to login to your account:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <span style="font-size: 42px; font-weight: bold;
                                         letter-spacing: 10px; color: #22c55e;">
                                %s
                            </span>
                        </div>
                        <p style="color: #888; font-size: 13px;">
                            ⏱ This code expires in <strong>5 minutes</strong>.
                        </p>
                        <p style="color: #888; font-size: 13px;">
                            If you did not request this code, please ignore this email.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(otpCode);
    }
}