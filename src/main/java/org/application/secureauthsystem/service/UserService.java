package org.application.secureauthsystem.service;

import lombok.AllArgsConstructor;
import org.application.secureauthsystem.mapper.UserMapper;
import org.application.secureauthsystem.model.entity.User;
import org.application.secureauthsystem.model.request.UpdateProfileRequest;
import org.application.secureauthsystem.model.response.ApiResponse;
import org.application.secureauthsystem.model.request.RegisterRequest;
import org.application.secureauthsystem.model.dto.UserDTO;
import org.application.secureauthsystem.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // Auth
    ApiResponse<UserDTO> create(RegisterRequest request);       // register

    // CRUD
    ApiResponse<UserDTO> getMe(String email);                  // get own profile
    ApiResponse<UserDTO> getById(UUID id);                     // get by id
    ApiResponse<List<UserDTO>> getAll();                       // get all users
    ApiResponse<UserDTO> update(String email, UpdateProfileRequest request); // update profile
    ApiResponse<Void> delete(UUID id);                         // delete user
}
