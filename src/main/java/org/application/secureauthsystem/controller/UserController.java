package org.application.secureauthsystem.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.application.secureauthsystem.model.dto.UserDTO;
import org.application.secureauthsystem.model.request.UpdateProfileRequest;
import org.application.secureauthsystem.model.response.ApiResponse;
import org.application.secureauthsystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getMe(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(userService.getMe(userDetails.getUsername()));
    }

    // ── Get All Users ────────────────────────────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAll() {
        return ResponseEntity
                .ok(userService.getAll());
    }

    // ── Get User By ID ───────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getById(
            @PathVariable UUID id) {
        return ResponseEntity
                .ok(userService.getById(id));
    }

    // ── Update My Profile ────────────────────────────────────
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity
                .ok(userService.update(userDetails.getUsername(), request));
    }

    // ── Delete User ──────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id) {
        return ResponseEntity
                .ok(userService.delete(id));
    }
}
