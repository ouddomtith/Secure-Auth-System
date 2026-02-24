package org.application.secureauthsystem.service.implementation;

import lombok.RequiredArgsConstructor;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.Subscription;
import org.application.secureauthsystem.model.entity.PushSubscription;
import org.application.secureauthsystem.model.entity.User;
import org.application.secureauthsystem.model.request.PushNotificationRequest;
import org.application.secureauthsystem.model.request.PushSubscriptionRequest;
import org.application.secureauthsystem.model.response.ApiResponse;
import org.application.secureauthsystem.repository.PushSubscriptionRepository;
import org.application.secureauthsystem.repository.UserRepository;
import org.application.secureauthsystem.service.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PushServiceImpl implements PushService {

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final UserRepository userRepository;

    @Value("${app.vapid.public-key}")
    private String vapidPublicKey;

    @Value("${app.vapid.private-key}")
    private String vapidPrivateKey;

    @Value("${app.vapid.subject}")
    private String vapidSubject;

    // ── Subscribe ────────────────────────────────────────────
    @Override
    public ApiResponse<Void> subscribe(String email, PushSubscriptionRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        pushSubscriptionRepository.findByEndpoint(request.getEndpoint())
                .ifPresentOrElse(
                        existing -> {
                        },
                        () -> pushSubscriptionRepository.save(
                                PushSubscription.builder()
                                        .endpoint(request.getEndpoint())
                                        .p256dh(request.getP256dh())
                                        .auth(request.getAuth())
                                        .user(user)
                                        .build()
                        )
                );

        return ApiResponse.<Void>builder()
                .message("Subscribed successfully")
                .status(HttpStatus.OK)
                .build();
    }

    // ── Unsubscribe ──────────────────────────────────────────
    @Override
    public ApiResponse<Void> unsubscribe(String endpoint) {
        pushSubscriptionRepository.findByEndpoint(endpoint)
                .ifPresent(pushSubscriptionRepository::delete);

        return ApiResponse.<Void>builder()
                .message("Unsubscribed successfully")
                .status(HttpStatus.OK)
                .build();
    }

    // ── Send To All ──────────────────────────────────────────
    @Override
    public ApiResponse<Void> sendToAll(PushNotificationRequest request) {
        sendPush(pushSubscriptionRepository.findAll(), request);

        return ApiResponse.<Void>builder()
                .message("Notification sent to all users")
                .status(HttpStatus.OK)
                .build();
    }

    // ── Send To One User ─────────────────────────────────────
    @Override
    public ApiResponse<Void> sendToUser(String email, PushNotificationRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        sendPush(pushSubscriptionRepository.findByUser(user), request);

        return ApiResponse.<Void>builder()
                .message("Notification sent to " + email)
                .status(HttpStatus.OK)
                .build();
    }

    // ── Private Helper ───────────────────────────────────────
    private void sendPush(List<PushSubscription> subscriptions,
                          PushNotificationRequest request) {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        try {
            nl.martijndwars.webpush.PushService pushService =
                    new nl.martijndwars.webpush.PushService(
                            vapidPublicKey,
                            vapidPrivateKey,
                            vapidSubject
                    );

            String payload = String.format(
                    "{\"title\":\"%s\",\"body\":\"%s\",\"url\":\"%s\"}",
                    request.getTitle(),
                    request.getBody(),
                    request.getUrl() != null ? request.getUrl() : "/"
            );

            for (PushSubscription sub : subscriptions) {
                try {
                    pushService.send(new Notification(
                            new Subscription(
                                    sub.getEndpoint(),
                                    new Subscription.Keys(
                                            sub.getP256dh(),
                                            sub.getAuth()
                                    )
                            ),
                            payload
                    ));
                } catch (Exception e) {
                    System.err.println("Failed to send to: " + sub.getEndpoint());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Push failed: " + e.getMessage());
        }
    }
}