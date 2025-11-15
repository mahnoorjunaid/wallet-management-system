package com.wallet.walletmanagement.controller;
import com.wallet.walletmanagement.service.UserService;
import com.wallet.walletmanagement.model.Transaction;
import com.wallet.walletmanagement.model.Wallet;
import com.wallet.walletmanagement.model.User; // <--- ADD THIS LINE
import com.wallet.walletmanagement.repository.TransactionRepository;
import com.wallet.walletmanagement.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;
    private final TransactionRepository transactionRepository;
    private final Random random = new Random();
    private final UserService userService;
    private final String[] merchants = {
            "Starbucks", "Amazon", "Walmart", "Apple Store", "Netflix",
            "Spotify", "Uber", "Airbnb", "Target", "Best Buy"
    };

    public WalletController(WalletService walletService,
                            TransactionRepository transactionRepository,
                            UserService userService) { // <-- ADD THIS INJECTION
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
        this.userService = userService; // <-- ASSIGN THIS
    }

    // -------------------------
    // API 18 — Create Wallet
    // -------------------------
    // In WalletController.java
// API 18 — Create Wallet
    @PostMapping("/create/{userId}")
    public ResponseEntity<?> createWallet(@PathVariable Long userId) {
        try {
            // FIX: Get User directly, which we know exists.
            User user = userService.getUserById(userId);

            // Pass the user to the createWallet service method.
            Wallet wallet = walletService.createWallet(user);

            return ResponseEntity.ok(wallet);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // In WalletController.java (add this method)
// API 22 — Update Wallet Balance (admin)
    @PutMapping("/{userId}/balance")
    public ResponseEntity<?> updateWalletBalance(@PathVariable Long userId,
                                                 @RequestBody Map<String, Object> body) {
        try {
            Double newBalance = Double.valueOf(body.get("newBalance").toString());
            Wallet updatedWallet = walletService.updateWalletBalance(userId, newBalance);
            return ResponseEntity.ok(updatedWallet);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    // In WalletController.java (add this method)
// API 24 — List All Wallets (Admin)
    @GetMapping("/all") // <--- Using a specific path /api/wallets/all
    public ResponseEntity<List<Wallet>> getAllWallets() {
        List<Wallet> wallets = walletService.getAllWallets();
        return ResponseEntity.ok(wallets);
    }
    // -------------------------
    // API 5 — Fund Loading via Bank Transfer
    // -------------------------
    @PostMapping("/{userId}/funds")
    public ResponseEntity<?> loadFunds(@PathVariable Long userId,
                                       @RequestBody Map<String, Object> body) {
        try {
            Double amount = Double.valueOf(body.get("amount").toString());
            Wallet updated = walletService.loadFunds(userId, amount);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // -------------------------
    // API 6 — Peer-to-Peer Fund Transfer
    // -------------------------
    @PostMapping("/transfer")
    public ResponseEntity<?> transferFunds(@RequestBody Map<String, Object> body) {
        try {
            Long fromUserId = Long.valueOf(body.get("fromUserId").toString());
            Long toUserId = Long.valueOf(body.get("toUserId").toString());
            Double amount = Double.valueOf(body.get("amount").toString());
            walletService.transferFunds(fromUserId, toUserId, amount);
            return ResponseEntity.ok("Transfer successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // -------------------------
    // API 7 — QR Code Payment to Merchant
    // -------------------------
    @PostMapping("/{userId}/pay")
    public ResponseEntity<?> payMerchant(@PathVariable Long userId,
                                         @RequestBody Map<String, Object> body) {
        try {
            Double amount = Double.valueOf(body.get("amount").toString());
            String merchantName = body.get("merchantName") != null ?
                    body.get("merchantName").toString() : "Merchant";

            walletService.payMerchant(userId, amount, merchantName);

            Wallet wallet = walletService.getWalletByUserId(userId);
            Transaction latestTransaction = transactionRepository.findTopByWalletOrderByTimestampDesc(wallet);

            return ResponseEntity.ok(Map.of(
                    "message", "Payment successful",
                    "wallet", wallet,
                    "transaction", latestTransaction
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // -------------------------
    // API 4 & 19 — Get Wallet by User
    // -------------------------
    @GetMapping("/{userId}")
    public ResponseEntity<?> getWallet(@PathVariable Long userId) {
        try {
            Wallet wallet = walletService.getWalletByUserId(userId);
            return ResponseEntity.ok(wallet);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // -------------------------
    // API 11 — Initiate Withdrawal
    // -------------------------
    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<?> withdrawFunds(@PathVariable Long userId,
                                           @RequestBody Map<String, Object> body) {
        try {
            Double amount = Double.valueOf(body.get("amount").toString());
            Wallet wallet = walletService.withdrawFunds(userId, amount);
            return ResponseEntity.ok(wallet);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // -------------------------
    // API 8 & 20 — Transaction History / Details
    // -------------------------
    @GetMapping("/{walletId}/transactions")
    public ResponseEntity<?> getTransactions(@PathVariable Long walletId) {
        try {
            List<Transaction> transactions = walletService.getTransactions(walletId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // -------------------------
    // API 9 — Set Spending Limit
    // -------------------------
    @PostMapping("/{userId}/limit")
    public ResponseEntity<?> setSpendingLimit(@PathVariable Long userId,
                                              @RequestBody Map<String, Object> body) {
        try {
            Double limit = Double.valueOf(body.get("limit").toString());
            Wallet wallet = walletService.setSpendingLimit(userId, limit);
            return ResponseEntity.ok(wallet);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/{userId}/notify-payment")
    public ResponseEntity<?> receivePaymentNotification(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> body) {
        try {
            Double amount = Double.valueOf(body.get("amount").toString());
            String description = body.get("description") != null ? body.get("description").toString() : "Bank deposit";

            Wallet updatedWallet = walletService.receivePaymentNotification(userId, amount, description);

            return ResponseEntity.ok(Map.of(
                    "message", "Payment notification processed",
                    "wallet", updatedWallet
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/statement")
    public ResponseEntity<?> getMonthlyStatement(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month) {

        try {
            List<Transaction> statement = walletService.getMonthlyStatement(userId, year, month);
            return ResponseEntity.ok(statement);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/transactions/{transactionId}/dispute")
    public ResponseEntity<?> disputeTransaction(@PathVariable Long transactionId) {
        try {
            Transaction disputedTx = walletService.disputeTransaction(transactionId);
            return ResponseEntity.ok(Map.of(
                    "message", "Transaction disputed successfully",
                    "transaction", disputedTx
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }



}
