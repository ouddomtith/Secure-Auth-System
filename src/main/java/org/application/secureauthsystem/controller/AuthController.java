package org.application.secureauthsystem.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.application.secureauthsystem.model.request.LoginRequest;
import org.application.secureauthsystem.model.request.RegisterRequest;
import org.application.secureauthsystem.model.request.VerifyOtpRequest;
import org.application.secureauthsystem.model.response.ApiResponse;
import org.application.secureauthsystem.model.response.AuthResponse;
import org.application.secureauthsystem.service.AuthService;
import org.application.secureauthsystem.service.OtpService;
import org.application.secureauthsystem.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final OtpService otpService;


    // ── Register ──
    @PostMapping("/register")
    public ResponseEntity<AuthResponse<?>> register(@Valid @RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity
                .ok(authService.login(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity
                .ok(authService.verifyOtp(request));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<String>> resendOtp(
            @RequestBody Map<String, String> body) {
        otpService.generateAndSendOtp(body.get("email"));
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .message("OTP resent successfully")
                        .status(HttpStatus.OK)
                        .build()
        );
    }
}
