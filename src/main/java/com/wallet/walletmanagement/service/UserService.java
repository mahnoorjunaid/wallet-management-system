package com.wallet.walletmanagement.service;
import java.util.UUID;
import com.wallet.walletmanagement.exception.ResourceNotFoundException;
import com.wallet.walletmanagement.model.User;
import com.wallet.walletmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // API 1 — Register User
    public User registerUser(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(u -> { throw new RuntimeException("Email already exists"); });
        return userRepository.save(user);
    }
    // In UserService.java (add this method)
    @Transactional
    public User reportLostDevice(Long userId) {
        User user = getUserById(userId);
        // 1. Reset password to a temporary, secure placeholder
        String tempPassword = UUID.randomUUID().toString();
        user.setPassword(tempPassword);

        // 2. Disable 2FA (since the device is compromised)
        user.setTwoFactorEnabled(false);

        // 3. Deactivate the user account for maximum safety (optional, but good practice)
        user.setActive(false);

        userRepository.save(user);
        // In a real system, you'd send the tempPassword to the user's alternate email/phone.
        // We will return the temp password here for testing.
        return user;
    }
    // API 2 — Get User by ID
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    // API 3 — Update User Details
    public User updateUser(Long userId, User updatedUser) {
        User user = getUserById(userId);
        if (updatedUser.getName() != null) user.setName(updatedUser.getName());
        if (updatedUser.getEmail() != null) user.setEmail(updatedUser.getEmail());
        if (updatedUser.getPassword() != null) user.setPassword(updatedUser.getPassword());
        return userRepository.save(user);
    }

    // API 13 — 2FA Setup
    public User setupTwoFactor(Long userId, boolean enabled) {
        User user = getUserById(userId);
        user.setTwoFactorEnabled(enabled);
        return userRepository.save(user);
    }

    // Admin APIs

    // API 26 — Admin Reset User Password
    public User adminResetPassword(Long userId, String newPassword) {
        return resetPassword(userId, newPassword);
    }

    // API 27 — Admin Activate User
    public User adminActivateUser(Long userId) {
        User user = getUserById(userId);
        user.setActive(true);
        return userRepository.save(user);
    }

    // API 16 — Account Deactivation
    public User deactivateUser(Long userId) {
        User user = getUserById(userId);
        user.setActive(false);
        return userRepository.save(user);
    }

    // API 29 — Admin Delete User
    public void adminDeleteUser(Long userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }

    // API 23 — List All Users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    // API 12 — Password Reset
    @Transactional
    public User resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setPassword(newPassword);
        return userRepository.save(user);
    }


}
