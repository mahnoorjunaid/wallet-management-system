package com.wallet.walletmanagement.service;

import com.wallet.walletmanagement.exception.ResourceNotFoundException;
import com.wallet.walletmanagement.model.Transaction;
import com.wallet.walletmanagement.model.Wallet;
import com.wallet.walletmanagement.repository.TransactionRepository;
import com.wallet.walletmanagement.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    // API 8 — Transaction History Retrieval
    public List<Transaction> getTransactionsByWalletId(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
        return transactionRepository.findByWallet(wallet);
    }

    // API 20 — Get Transaction Details
    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));
    }

    // API 21 — Delete Transaction
    public void deleteTransaction(Long transactionId) {
        Transaction transaction = getTransactionById(transactionId);
        transactionRepository.delete(transaction);
    }

    // API 25 — List All Transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Optional helper: create a transaction (used by WalletService for credit/debit)
    public Transaction createTransaction(Wallet wallet, String type, Double amount) {
        Transaction transaction = new Transaction(wallet, type, amount);
        return transactionRepository.save(transaction);
    }
}
