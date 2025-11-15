package com.wallet.walletmanagement.controller;

import com.wallet.walletmanagement.dto.TransactionDTO;
import com.wallet.walletmanagement.model.Transaction;
import com.wallet.walletmanagement.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Helper: Transaction -> TransactionDTO
    private TransactionDTO toDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getWallet().getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getTimestamp()
        );
    }

    // API 20 — Transaction Details by ID
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long transactionId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(toDTO(transaction));
    }

    // API 21 — Delete Transaction
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.ok("Transaction " + transactionId + " deleted successfully");
    }

    // API 25 — List All Transactions
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        List<TransactionDTO> dtos = transactions.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
