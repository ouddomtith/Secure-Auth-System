package org.application.secureauthsystem.service;

import org.application.secureauthsystem.model.entity.User;

public interface TrustedDeviceService {
    String generateDeviceToken(User user);
    boolean isTrustedDevice(String deviceToken);
    void removeTrustedDevice(String deviceToken);
    void removeAllExpiredDevices(User user);
}