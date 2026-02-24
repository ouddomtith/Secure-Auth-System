package org.application.secureauthsystem.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PushNotificationRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    private String body;

    private String url;
}
