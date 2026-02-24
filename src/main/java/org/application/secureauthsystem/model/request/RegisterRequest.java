package org.application.secureauthsystem.model.request;

import jakarta.validation.constraints.Email;       // ✅ will work
import jakarta.validation.constraints.NotBlank;    // ✅ will work
import jakarta.validation.constraints.Size;        // ✅ will work

import lombok.Data;
import org.application.secureauthsystem.model.entity.User;

import java.util.UUID;

@Data
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
