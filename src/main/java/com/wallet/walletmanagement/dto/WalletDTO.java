package com.wallet.walletmanagement.dto;

public class WalletDTO {
    private Long userId;
    private Double balance;

    public WalletDTO() {}

    public WalletDTO(Long userId, Double balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
}
