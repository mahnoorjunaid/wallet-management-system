package com.wallet.walletmanagement.dto;

public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private boolean twoFactorEnabled;
    private boolean active;

    public UserDTO(Long id, String name, String email, boolean twoFactorEnabled, boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.twoFactorEnabled = twoFactorEnabled;
        this.active = active;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isTwoFactorEnabled() { return twoFactorEnabled; }
    public void setTwoFactorEnabled(boolean twoFactorEnabled) { this.twoFactorEnabled = twoFactorEnabled; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
