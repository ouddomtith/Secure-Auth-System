package org.application.secureauthsystem.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otpCode);
}