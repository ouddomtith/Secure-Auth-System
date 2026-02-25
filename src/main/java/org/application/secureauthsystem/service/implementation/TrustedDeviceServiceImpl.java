package org.application.secureauthsystem.service.implementation;

import org.application.secureauthsystem.model.entity.TrustedDevice;
import org.application.secureauthsystem.model.entity.User;
import org.application.secureauthsystem.repository.TrustedDeviceRepository;
import org.application.secureauthsystem.service.TrustedDeviceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TrustedDeviceServiceImpl implements TrustedDeviceService {

    private final TrustedDeviceRepository trustedDeviceRepository;

    @Value("${app.trusted-device.expiration-minutes}")
    private int expirationMinutes;

    public TrustedDeviceServiceImpl(TrustedDeviceRepository trustedDeviceRepository) {
        this.trustedDeviceRepository = trustedDeviceRepository;
    }

    // ── Generate Device Token ────────────────────────────
    @Override
    @Transactional
    public String generateDeviceToken(User user) {
        // Clean up expired devices first
        removeAllExpiredDevices(user);

        // Generate unique token
        String deviceToken = UUID.randomUUID().toString();

        // Save to DB
        TrustedDevice device = TrustedDevice.builder()
                .deviceToken(deviceToken)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
                .build();

        trustedDeviceRepository.save(device);

        return deviceToken;
    }

    // ── Check If Device Is Trusted ───────────────────────
    @Override
    public boolean isTrustedDevice(String deviceToken) {
        // ← Make sure null check is here
        if (deviceToken == null || deviceToken.isEmpty()) {
            return false;
        }

        return trustedDeviceRepository
                .findByDeviceToken(deviceToken)
                .map(device -> !device.isExpired())
                .orElse(false);
    }

    // ── Remove One Device ────────────────────────────────
    @Override
    @Transactional
    public void removeTrustedDevice(String deviceToken) {
        trustedDeviceRepository.findByDeviceToken(deviceToken)
                .ifPresent(trustedDeviceRepository::delete);
    }

    // ── Remove All Expired Devices ───────────────────────
    @Override
    @Transactional
    public void removeAllExpiredDevices(User user) {
        trustedDeviceRepository.deleteByUserAndExpiresAtBefore(
                user,
                LocalDateTime.now()
        );
    }
    // ── Check If User Has Any Active Trusted Device ──────
    @Override
    public boolean hasActiveTrustedDevice(User user) {
        return trustedDeviceRepository
                .existsByUserAndExpiresAtAfter(user, LocalDateTime.now());
    }

}