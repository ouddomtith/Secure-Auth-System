package org.application.secureauthsystem.service.implementation;

import lombok.AllArgsConstructor;
import org.application.secureauthsystem.mapper.UserMapper;
import org.application.secureauthsystem.model.entity.User;
import org.application.secureauthsystem.model.request.LoginRequest;
import org.application.secureauthsystem.model.request.RegisterRequest;
import org.application.secureauthsystem.model.request.VerifyOtpRequest;
import org.application.secureauthsystem.model.response.ApiResponse;
import org.application.secureauthsystem.model.response.AuthResponse;
import org.application.secureauthsystem.repository.UserRepository;
import org.application.secureauthsystem.security.JwtService;
import org.application.secureauthsystem.service.AuthService;
import org.application.secureauthsystem.service.OtpService;
import org.application.secureauthsystem.service.TrustedDeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final AuthenticationManager authenticationManager;
    private final TrustedDeviceService trustedDeviceService;
    private final UserMapper userMapper;
    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Save user
        User savedUser = userRepository.save(user);

        // Generate token
        String token = jwtService.generateToken(savedUser.getEmail());

        // Return response
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userMapper.toDTO(savedUser))
                .build();
    }

    @Override
    public ApiResponse<String> login(LoginRequest request) {
        // Authenticate
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Get user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Check trusted device
        // ← Add this null check!
        if (request.getDeviceToken() != null &&
                trustedDeviceService.isTrustedDevice(request.getDeviceToken())) {
            String token = jwtService.generateToken(user.getEmail());
            return ApiResponse.<String>builder()
                    .message("Login successful (trusted device)")
                    .payload(token)
                    .status(HttpStatus.OK)
                    .build();
        }

        // Send OTP
        otpService.generateAndSendOtp(user.getEmail());

        return ApiResponse.<String>builder()
                .message("OTP sent to " + user.getEmail())
                .payload(null)
                .status(HttpStatus.OK)
                .build();
    }

    @Override
    public ApiResponse<AuthResponse> verifyOtp(VerifyOtpRequest request) {
        // Step 1 - Verify OTP code
        otpService.verifyOtp(request.getEmail(), request.getOtpCode());

        // Step 2 - Get user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Step 3 - Generate JWT
        String token = jwtService.generateToken(user.getEmail());

        // Step 4 - Trust device if requested
        String deviceToken = null;
        if (request.isTrustDevice()) {
            deviceToken = trustedDeviceService.generateDeviceToken(user);
        }

        // Step 5 - Return response
        return ApiResponse.<AuthResponse>builder()
                .message("Login successful")
                .payload(AuthResponse.builder()
                        .token(token)
                        .tokenType("Bearer")
                        .deviceToken(deviceToken)
                        .user(userMapper.toDTO(user))
                        .build())
                .status(HttpStatus.OK)
                .build();
    }
}
