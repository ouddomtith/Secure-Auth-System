package org.application.secureauthsystem.repository;

import org.application.secureauthsystem.model.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    // Get latest OTP by email
    Optional<OtpCode> findTopByEmailOrderByCreatedAtDesc(String email);

    // Delete all OTPs for an email (cleanup after verify)
    void deleteAllByEmail(String email);
}