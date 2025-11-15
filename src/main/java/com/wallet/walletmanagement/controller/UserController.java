package com.wallet.walletmanagement.controller;

import com.wallet.walletmanagement.dto.UserDTO;
import com.wallet.walletmanagement.model.User;
import com.wallet.walletmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    // API 1 — Register User
    @PostMapping("/register")
    public UserDTO registerUser(@RequestBody User user) {
        return toDTO(userService.registerUser(user));
    }
    // In UserController.java
// API 16 — Account Deactivation
    @PutMapping("/{userId}/deactivate")
    public UserDTO deactivateUser(@PathVariable Long userId) {
        return toDTO(userService.deactivateUser(userId));
    }

    // API 2 — Get User by ID
    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable Long userId) {
        return toDTO(userService.getUserById(userId));
    }

    // API 3 — Update User Details
    @PutMapping("/{userId}")
    public UserDTO updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        return toDTO(userService.updateUser(userId, updatedUser));
    }
    // In UserController.java (add this method)
// API 17 — Report Lost/Stolen Device
    @PostMapping("/{userId}/report-lost")
    public ResponseEntity<Map<String, Object>> reportLostDevice(@PathVariable Long userId) {
        User user = userService.reportLostDevice(userId);

        return ResponseEntity.ok(Map.of(
                "message", "Device reported lost. Account temporarily secured.",
                "userId", user.getId(),
                "accountActive", user.isActive(),
                "twoFactorEnabled", user.isTwoFactorEnabled(),
                "NOTE", "New password is set internally (check UserService for value)"
        ));
    }
    // API 12 — Password Reset
    // API 13 — 2FA Setup
    // API 12 — Password Reset
    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long userId,
                                           @RequestBody Map<String, Object> body) {
        try {
            String newPassword = body.get("newPassword").toString();
            User updatedUser = userService.resetPassword(userId, newPassword);
            return ResponseEntity.ok(toDTO(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // API 13 — 2FA Setup
    @PostMapping("/2fa/{userId}")
    public ResponseEntity<?> setupTwoFactor(@PathVariable Long userId,
                                            @RequestBody Map<String, Object> body) {
        try {
            boolean enabled = Boolean.parseBoolean(body.get("enabled").toString());
            User updatedUser = userService.setupTwoFactor(userId, enabled);
            return ResponseEntity.ok(toDTO(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // Helper to convert User -> UserDTO
    private UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isTwoFactorEnabled(),
                user.isActive()
        );
    }



    // API 23 — List All Users
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
