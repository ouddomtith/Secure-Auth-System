package org.application.secureauthsystem.service.implementation;

import lombok.AllArgsConstructor;
import org.application.secureauthsystem.mapper.UserMapper;
import org.application.secureauthsystem.model.entity.User;
import org.application.secureauthsystem.model.request.UpdateProfileRequest;
import org.application.secureauthsystem.model.response.ApiResponse;
import org.application.secureauthsystem.model.request.RegisterRequest;
import org.application.secureauthsystem.model.dto.UserDTO;
import org.application.secureauthsystem.repository.UserRepository;
import org.application.secureauthsystem.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    // ── Register ────────────────────────────────────────────
    @Override
    public ApiResponse<UserDTO> create(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return ApiResponse.<UserDTO>builder()
                .message("Register Successfully")
                .payload(userMapper.toDTO(userRepository.save(user)))
                .status(HttpStatus.CREATED)
                .build();
    }

    // ── Get My Profile ───────────────────────────────────────
    @Override
    public ApiResponse<UserDTO> getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return ApiResponse.<UserDTO>builder()
                .message("User fetched successfully")
                .payload(userMapper.toDTO(user))
                .status(HttpStatus.OK)
                .build();
    }

    // ── Get By ID ────────────────────────────────────────────
    @Override
    public ApiResponse<UserDTO> getById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return ApiResponse.<UserDTO>builder()
                .message("User fetched successfully")
                .payload(userMapper.toDTO(user))
                .status(HttpStatus.OK)
                .build();
    }

    // ── Get All Users ────────────────────────────────────────
    @Override
    public ApiResponse<List<UserDTO>> getAll() {
        List<UserDTO> users = userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .toList();

        return ApiResponse.<List<UserDTO>>builder()
                .message("Users fetched successfully")
                .payload(users)
                .status(HttpStatus.OK)
                .build();
    }

    // ── Update Profile ───────────────────────────────────────
    @Override
    public ApiResponse<UserDTO> update(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (request.getName()      != null) user.setName(request.getName());
        if (request.getBio()       != null) user.setBio(request.getBio());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());

        return ApiResponse.<UserDTO>builder()
                .message("Profile updated successfully")
                .payload(userMapper.toDTO(userRepository.save(user)))
                .status(HttpStatus.OK)
                .build();
    }

    // ── Delete User ──────────────────────────────────────────
    @Override
    public ApiResponse<Void> delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found!");
        }

        userRepository.deleteById(id);

        return ApiResponse.<Void>builder()
                .message("User deleted successfully")
                .status(HttpStatus.OK)
                .build();
    }
}
