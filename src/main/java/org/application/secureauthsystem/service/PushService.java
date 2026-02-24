package org.application.secureauthsystem.service;

import org.application.secureauthsystem.model.request.PushNotificationRequest;
import org.application.secureauthsystem.model.request.PushSubscriptionRequest;
import org.application.secureauthsystem.model.response.ApiResponse;

public interface PushService {
    ApiResponse<Void> subscribe(String email, PushSubscriptionRequest request);
    ApiResponse<Void> unsubscribe(String endpoint);
    ApiResponse<Void> sendToAll(PushNotificationRequest request);
    ApiResponse<Void> sendToUser(String email, PushNotificationRequest request);
}
