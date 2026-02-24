package org.application.secureauthsystem.service.implementation;

import lombok.AllArgsConstructor;
import org.application.secureauthsystem.model.entity.OtpCode;
import org.application.secureauthsystem.repository.OtpCodeRepository;
import org.application.secureauthsystem.service.EmailService;
import org.application.secureauthsystem.service.OtpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpServiceImpl implements OtpService {

    private final OtpCodeRepository otpCodeRepository;
    private final EmailService emailService;

    @Value("${app.otp.expiration-minutes}")
    private int expirationMinutes;

    public OtpServiceImpl(OtpCodeRepository otpCodeRepository,
                          EmailService emailService) {
        this.otpCodeRepository = otpCodeRepository;
        this.emailService = emailService;
    }

    // ── Generate and Send OTP ────────────────────────────
    @Override
    @Transactional
    public void generateAndSendOtp(String email) {
        // Delete old OTPs for this email
        otpCodeRepository.deleteAllByEmail(email);

        // Generate 6 digit OTP
        String code = generateCode();

        // Save to DB
        OtpCode otp = OtpCode.builder()
                .code(code)
                .email(email)
                .used(false)
                .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
                .build();

        otpCodeRepository.save(otp);

        // Send email
        emailService.sendOtpEmail(email, code);
    }

    // ── Verify OTP ───────────────────────────────────────
    @Override
    @Transactional
    public boolean verifyOtp(String email, String code) {
        OtpCode otp = otpCodeRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP not found!"));

        // Check valid
        if (!otp.isValid()) {
            throw new RuntimeException("OTP has expired or already been used!");
        }

        // Check code matches
        if (!otp.getCode().equals(code)) {
            throw new RuntimeException("Invalid OTP code!");
        }

        // Mark as used
        otp.setUsed(true);
        otpCodeRepository.save(otp);

        // Cleanup
        otpCodeRepository.deleteAllByEmail(email);

        return true;
    }

    // ── Generate 6 Digit Code ────────────────────────────
    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}