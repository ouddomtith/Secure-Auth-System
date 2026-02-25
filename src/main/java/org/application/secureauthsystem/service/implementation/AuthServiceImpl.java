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

    // ── Login ────────────────────────────────────────────
    @Override
    public ApiResponse<AuthResponse> login(LoginRequest request) {

        // Step 1 - Authenticate email + password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Step 2 - Get user from DB
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Step 3 - Check if user has active trusted device in DB
        if (trustedDeviceService.hasActiveTrustedDevice(user)) {
            // ← Skip OTP, return JWT + user directly
            return ApiResponse.<AuthResponse>builder()
                    .message("Login successful (trusted device)")
                    .payload(AuthResponse.builder()
                            .token(jwtService.generateToken(user.getEmail()))
                            .tokenType("Bearer")
                            .deviceToken(null)
                            .user(userMapper.toDTO(user))
                            .build())
                    .status(HttpStatus.OK)
                    .build();
        }

        // Step 4 - No trusted device → send OTP
        otpService.generateAndSendOtp(user.getEmail());

        // ← Return null payload means OTP was sent
        return ApiResponse.<AuthResponse>builder()
                .message("OTP sent to " + user.getEmail())
                .payload(null)
                .status(HttpStatus.OK)
                .build();
    }

    // ── Verify OTP ───────────────────────────────────────
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

        // Step 5 - Return JWT + user
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
