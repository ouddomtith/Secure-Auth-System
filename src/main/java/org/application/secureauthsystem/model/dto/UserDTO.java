package org.application.secureauthsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.application.secureauthsystem.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;
    private String email;
    private String name;
    private String avatarUrl;
    private String bio;
    private Role role; // "USER" or "ADMIN"
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
