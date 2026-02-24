package org.application.secureauthsystem.model.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Size(max = 255, message = "Bio must not exceed 255 characters")
    private String bio;

    private String avatarUrl;
}
