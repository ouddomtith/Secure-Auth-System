package org.application.secureauthsystem.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.application.secureauthsystem.model.request.LoginRequest;
import org.application.secureauthsystem.model.request.RegisterRequest;
import org.application.secureauthsystem.model.request.VerifyOtpRequest;
import org.application.secureauthsystem.model.response.ApiResponse;
import org.application.secureauthsystem.model.response.AuthResponse;
import org.application.secureauthsystem.service.AuthService;
import org.application.secureauthsystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    // ── Register ──
    @PostMapping("/register")
    public ResponseEntity<AuthResponse<?>> register(@Valid @RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    // ── Verify OTP ───────────────────────────────────────
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity
                .ok(authService.verifyOtp(request));
    }
}
