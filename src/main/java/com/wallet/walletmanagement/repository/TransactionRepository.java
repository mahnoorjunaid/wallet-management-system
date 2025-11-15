package com.wallet.walletmanagement.repository;

import com.wallet.walletmanagement.model.Transaction;
import com.wallet.walletmanagement.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWallet(Wallet wallet);
    Transaction findTopByWalletOrderByTimestampDesc(Wallet wallet); // optional: for latest transaction
}
