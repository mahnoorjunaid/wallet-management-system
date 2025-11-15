package com.wallet.walletmanagement.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set; // <-- NEW: Import Set

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // This field links to the Transactions table
    // CascadeType.ALL ensures that if this Wallet is deleted,
    // ALL related Transaction records are automatically deleted too.
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transaction> transactions = new HashSet<>(); // <-- NEW: The collection of linked transactions

    @Column(nullable = false)
    private Double balance = 0.0;

    @Column(nullable = false)
    private Double spendingLimit = 0.0;

    @Column(nullable = false)
    private boolean active = true;

    // Constructors
    public Wallet() {}

    public Wallet(User user, Double balance) {
        this.user = user;
        this.balance = balance;
        this.spendingLimit = 0.0;
        this.active = true;
    }

    public Wallet(User user) {
        this.user = user;
        this.balance = 0.0;
        this.spendingLimit = 0.0;
        this.active = true;
    }

    // Getters and Setters (Only the new one is listed, others remain the same)

    public Long getId() { return id; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public Double getBalance() { return balance; }

    public void setBalance(Double balance) { this.balance = balance; }

    public Double getSpendingLimit() { return spendingLimit; }

    public void setSpendingLimit(Double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }

    // Getter/Setter for the new transactions set (Optional, but good practice)
    public Set<Transaction> getTransactions() { return transactions; }

    public void setTransactions(Set<Transaction> transactions) { this.transactions = transactions; }
}