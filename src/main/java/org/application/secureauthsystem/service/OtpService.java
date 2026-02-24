package org.application.secureauthsystem.service;

public interface OtpService {
    void generateAndSendOtp(String email);
    boolean verifyOtp(String email, String code);
}