package org.application.secureauthsystem.service;

import org.application.secureauthsystem.model.request.LoginRequest;
import org.application.secureauthsystem.model.request.RegisterRequest;
import org.application.secureauthsystem.model.request.VerifyOtpRequest;
import org.application.secureauthsystem.model.response.ApiResponse;
import org.application.secureauthsystem.model.response.AuthResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    AuthResponse register(RegisterRequest request);
    ApiResponse login(LoginRequest request);
    ApiResponse<AuthResponse> verifyOtp(VerifyOtpRequest request);

}
