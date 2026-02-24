package org.application.secureauthsystem.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.application.secureauthsystem.model.dto.UserDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse<T> {
    private String token;
    private String tokenType;
    private String deviceToken;
    private UserDTO user;
}
