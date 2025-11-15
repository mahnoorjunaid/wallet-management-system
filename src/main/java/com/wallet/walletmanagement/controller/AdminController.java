package com.wallet.walletmanagement.controller;

import com.wallet.walletmanagement.dto.UserDTO;
import com.wallet.walletmanagement.dto.WalletDTO;
import com.wallet.walletmanagement.model.User;
import com.wallet.walletmanagement.service.UserService;
import com.wallet.walletmanagement.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    // Helper method to map User -> UserDTO
    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.isActive(), user.isTwoFactorEnabled());
    }

    // API 26 — Admin Reset User Password
    @PostMapping("/reset-password/{userId}")
    public UserDTO adminResetPassword(@PathVariable Long userId, @RequestParam String newPassword) {
        User user = userService.adminResetPassword(userId, newPassword);
        return toDTO(user);
    }

    // API 27 — Admin Activate User
    @PostMapping("/activate-user/{userId}")
    public UserDTO adminActivateUser(@PathVariable Long userId) {
        User user = userService.adminActivateUser(userId);
        return toDTO(user);
    }

    // API 28 — Admin Deactivate Wallet
    @PostMapping("/deactivate-wallet/{walletId}")
    public ResponseEntity<String> adminDeactivateWallet(@PathVariable Long walletId) {
        walletService.deactivateWallet(walletId);
        return ResponseEntity.ok("Wallet deactivated");
    }

    // API 29 — Admin Delete User
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<String> adminDeleteUser(@PathVariable Long userId) {
        userService.adminDeleteUser(userId);
        return ResponseEntity.ok("User deleted");
    }

    // API 30 — Admin Delete Wallet
    @DeleteMapping("/delete-wallet/{walletId}")
    public ResponseEntity<String> adminDeleteWallet(@PathVariable Long walletId) {
        walletService.deleteWallet(walletId);
        return ResponseEntity.ok("Wallet deleted");
    }
}
