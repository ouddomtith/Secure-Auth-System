package org.application.secureauthsystem.repository;

import org.application.secureauthsystem.model.entity.TrustedDevice;
import org.application.secureauthsystem.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrustedDeviceRepository extends JpaRepository<TrustedDevice, Long> {

    // Find device by token
    Optional<TrustedDevice> findByDeviceToken(String deviceToken);

    // Find all devices for a user
    List<TrustedDevice> findByUser(User user);

    // Delete all expired devices for a user
    void deleteByUserAndExpiresAtBefore(User user, java.time.LocalDateTime now);
}