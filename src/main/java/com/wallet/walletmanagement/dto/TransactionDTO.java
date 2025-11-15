package com.wallet.walletmanagement.dto;

import java.time.LocalDateTime;

public class TransactionDTO {
    private Long id;
    private Long walletId;
    private String type;
    private Double amount;
    private LocalDateTime timestamp;

    public TransactionDTO() {}

    public TransactionDTO(Long id, Long walletId, String type, Double amount, LocalDateTime timestamp) {
        this.id = id;
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getWalletId() { return walletId; }
    public void setWalletId(Long walletId) { this.walletId = walletId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
