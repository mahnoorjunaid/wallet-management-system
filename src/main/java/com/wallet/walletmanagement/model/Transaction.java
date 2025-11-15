package com.wallet.walletmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    private String type; // CREDIT or DEBIT
    private Double amount;

    private LocalDateTime timestamp = LocalDateTime.now();

    private String description;  // NEW FIELD

    public Transaction() {}

    // Minimal constructor
    public Transaction(Wallet wallet, String type, Double amount) {
        this.wallet = wallet;
        this.type = type;
        this.amount = amount;
    }
    @Column(nullable = false)
    private boolean disputed = false;

    // Getter & Setter
    public boolean isDisputed() { return disputed; }
    public void setDisputed(boolean disputed) { this.disputed = disputed; }

    // Full constructor for DataLoader
    public Transaction(Wallet wallet, String type, Double amount, LocalDateTime timestamp, String description) {
        this.wallet = wallet;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.description = description;
    }

    public Long getId() { return id; }
    public Wallet getWallet() { return wallet; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
