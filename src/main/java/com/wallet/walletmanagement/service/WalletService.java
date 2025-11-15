package com.wallet.walletmanagement.service;

import com.wallet.walletmanagement.exception.ResourceNotFoundException;
import com.wallet.walletmanagement.model.Transaction;
import com.wallet.walletmanagement.model.User;
import com.wallet.walletmanagement.model.Wallet;
import com.wallet.walletmanagement.repository.TransactionRepository;
import com.wallet.walletmanagement.repository.UserRepository;
import com.wallet.walletmanagement.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.time.YearMonth;
import java.time.LocalDateTime;
import java.util.stream.Collectors;


@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // -------------------------
    // Helper validations
    // -------------------------
    private void validateAmount(Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    private void ensureWalletActive(Wallet wallet) {
        if (wallet == null) throw new IllegalArgumentException("Wallet is null");
        if (!wallet.isActive()) {
            throw new IllegalStateException("Wallet is deactivated");
        }
    }

    private void enforceSpendingLimit(Wallet wallet, Double amount) {
        Double limit = wallet.getSpendingLimit();
        if (limit != null && limit > 0 && amount > limit) {
            throw new IllegalStateException("Transaction exceeds spending limit of " + limit);
        }
    }

    // -------------------------
    // API 18 — Create Wallet
    // -------------------------
    public Wallet createWallet(User user) {
        // Confirm user exists (controller should already do this)
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Prevent duplicate wallets for same user
        Optional<Wallet> existing = walletRepository.findByUser(user);
        if (existing.isPresent()) {
            throw new IllegalStateException("User already has a wallet");
        }

        Wallet wallet = new Wallet(user, 0.0);
        //wallet.setSpendingLimit(null); // no limit by default
        wallet.setActive(true);
        return walletRepository.save(wallet);
    }

    // -------------------------
    // API 4 & 19 — Get Wallet by User
    // -------------------------
    public Wallet getWalletByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user id: " + userId));
    }
    // In WalletService.java (add this simple method)
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }
    // -------------------------
    // API 5 — Fund Loading via Bank Transfer
    // -------------------------
    @Transactional
    public Wallet loadFunds(Long userId, Double amount) {
        validateAmount(amount);

        Wallet wallet = getWalletByUserId(userId);
        ensureWalletActive(wallet);

        wallet.setBalance(roundTwoDecimals(wallet.getBalance() + amount));
        wallet = walletRepository.save(wallet);

        Transaction transaction = new Transaction(wallet, "CREDIT", amount);
        transactionRepository.save(transaction);

        return wallet;
    }

    // -------------------------
    // API 6 — Peer-to-Peer Fund Transfer
    // -------------------------
    @Transactional
    public void transferFunds(Long fromUserId, Long toUserId, Double amount) {
        validateAmount(amount);

        Wallet fromWallet = getWalletByUserId(fromUserId);
        Wallet toWallet = getWalletByUserId(toUserId);

        ensureWalletActive(fromWallet);
        ensureWalletActive(toWallet);

        // enforce spending limit on sender
        enforceSpendingLimit(fromWallet, amount);

        if (fromWallet.getBalance() < amount) {
            throw new IllegalStateException("Insufficient balance for transfer");
        }

        fromWallet.setBalance(roundTwoDecimals(fromWallet.getBalance() - amount));
        toWallet.setBalance(roundTwoDecimals(toWallet.getBalance() + amount));

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        transactionRepository.save(new Transaction(fromWallet, "DEBIT", amount));
        transactionRepository.save(new Transaction(toWallet, "CREDIT", amount));
    }

    // -------------------------
    // API 7 — QR Code Payment to Merchant
    // -------------------------
    // -------------------------
// API 7 — QR Code Payment to Merchant
// -------------------------
    @Transactional
    public void payMerchant(Long userId, Double amount, String merchantName) {
        validateAmount(amount);

        Wallet wallet = getWalletByUserId(userId);
        ensureWalletActive(wallet);

        // enforce spending limit
        enforceSpendingLimit(wallet, amount);

        if (wallet.getBalance() < amount) {
            throw new IllegalStateException("Insufficient balance for payment");
        }

        wallet.setBalance(roundTwoDecimals(wallet.getBalance() - amount));
        walletRepository.save(wallet);

        // Save transaction with merchant info
        Transaction transaction = new Transaction(wallet, "DEBIT", amount);
        transaction.setDescription("Paid to " + merchantName);
        transactionRepository.save(transaction);
    }


    // -------------------------
    // API 11 — Initiating a Withdrawal
    // -------------------------
    @Transactional
    public Wallet withdrawFunds(Long userId, Double amount) {
        validateAmount(amount);

        Wallet wallet = getWalletByUserId(userId);
        ensureWalletActive(wallet);

        enforceSpendingLimit(wallet, amount);

        if (wallet.getBalance() < amount) {
            throw new IllegalStateException("Insufficient balance for withdrawal");
        }

        wallet.setBalance(roundTwoDecimals(wallet.getBalance() - amount));
        wallet = walletRepository.save(wallet);

        Transaction transaction = new Transaction(wallet, "DEBIT", amount);
        transactionRepository.save(transaction);
        return wallet;
    }

    // -------------------------
    // API 22 — Update Wallet Balance (admin)
    // -------------------------
    public Wallet updateWalletBalance(Long userId, Double newBalance) {
        if (newBalance == null) throw new IllegalArgumentException("New balance required");
        Wallet wallet = getWalletByUserId(userId);
        wallet.setBalance(roundTwoDecimals(newBalance));
        return walletRepository.save(wallet);
    }

    // -------------------------
    // API 28 — Admin Deactivate Wallet
    // -------------------------
    public void deactivateWallet(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
        // mark inactive and optionally zero out balance
        wallet.setActive(false);
        wallet.setBalance(0.0);
        walletRepository.save(wallet);
    }

    // -------------------------
    // API 30 — Admin Delete Wallet
    // -------------------------
    public void deleteWallet(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
        // because Wallet entity uses cascade = CascadeType.ALL + orphanRemoval, transactions will be deleted automatically
        walletRepository.delete(wallet);
    }

    // -------------------------
    // API 8 & 20 — Transaction History / Details
    // -------------------------
    public List<Transaction> getTransactions(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
        return transactionRepository.findByWallet(wallet);
    }

    // -------------------------
    // API 9 — Setting Spending Limits
    // -------------------------
    public Wallet setSpendingLimit(Long userId, Double limit) {
        if (limit != null && limit < 0) throw new IllegalArgumentException("Spending limit cannot be negative");
        Wallet wallet = getWalletByUserId(userId);
        wallet.setSpendingLimit(limit);
        return walletRepository.save(wallet);
    }

    // -------------------------
    // Utility
    // -------------------------
    private Double roundTwoDecimals(Double value) {
        if (value == null) return null;
        return Math.round(value * 100.0) / 100.0;
    }

    @Transactional
    public Wallet receivePaymentNotification(Long userId, Double amount, String description) {
        validateAmount(amount);

        Wallet wallet = getWalletByUserId(userId);
        ensureWalletActive(wallet);

        // Update balance
        wallet.setBalance(roundTwoDecimals(wallet.getBalance() + amount));
        walletRepository.save(wallet);

        // Create transaction record
        Transaction transaction = new Transaction(wallet, "CREDIT", amount);
        transaction.setDescription(description);
        transactionRepository.save(transaction);

        return wallet;
    }
    public List<Transaction> getMonthlyStatement(Long userId, int year, int month) {
        Wallet wallet = getWalletByUserId(userId);

        YearMonth ym = YearMonth.of(year, month);

        return transactionRepository.findByWallet(wallet)
                .stream()
                .filter(tx -> {
                    LocalDateTime ts = tx.getTimestamp();
                    return ts.getYear() == ym.getYear() && ts.getMonthValue() == ym.getMonthValue();
                })
                .collect(Collectors.toList());
    }
    @Transactional
    public Transaction disputeTransaction(Long transactionId) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with id: " + transactionId));

        if (tx.isDisputed()) {
            throw new IllegalStateException("Transaction is already disputed");
        }

        tx.setDisputed(true);
        return transactionRepository.save(tx);
    }


}
