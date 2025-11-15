package com.wallet.walletmanagement.config;

import com.wallet.walletmanagement.model.Transaction;
import com.wallet.walletmanagement.model.User;
import com.wallet.walletmanagement.model.Wallet;
import com.wallet.walletmanagement.repository.TransactionRepository;
import com.wallet.walletmanagement.repository.UserRepository;
import com.wallet.walletmanagement.repository.WalletRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Configuration
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public DataLoader(UserRepository userRepository,
                      WalletRepository walletRepository,
                      TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            System.out.println("Demo data already exists, skipping DataLoader.");
            return;
        }

        // ----------------------------
        // Create 10 users
        // ----------------------------
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            User user = new User("User" + i, "user" + i + "@example.com", "password" + i);
            users.add(userRepository.save(user));
        }

        // ----------------------------
        // Create wallets with random balances
        // ----------------------------
        List<Wallet> wallets = new ArrayList<>();
        Random random = new Random();
        for (User user : users) {
            Wallet wallet = new Wallet(user, 500.0 + random.nextInt(1000)); // balance 500-1500
            wallet.setSpendingLimit(100.0 + random.nextInt(400)); // limit 100-500
            wallets.add(walletRepository.save(wallet));
        }

        // ----------------------------
        // Create 20 realistic transactions
        // ----------------------------
        if (transactionRepository.count() == 0) {
            List<Transaction> transactions = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                Wallet fromWallet = wallets.get(random.nextInt(wallets.size()));
                Wallet toWallet = wallets.get(random.nextInt(wallets.size()));
                double amount = 10 + random.nextInt(200); // $10-$210

                // Random timestamp within last 30 days
                LocalDateTime timestamp = LocalDateTime.now().minus(random.nextInt(30), ChronoUnit.DAYS);

                if (fromWallet != toWallet) {
                    // Peer-to-peer
                    fromWallet.setBalance(fromWallet.getBalance() - amount);
                    toWallet.setBalance(toWallet.getBalance() + amount);

                    transactionRepository.save(new Transaction(fromWallet, "DEBIT", amount, timestamp, "Paid " + toWallet.getUser().getName()));
                    transactionRepository.save(new Transaction(toWallet, "CREDIT", amount, timestamp, "Received from " + fromWallet.getUser().getName()));
                } else {
                    // Self transaction like bank load or withdrawal
                    boolean isCredit = random.nextBoolean();
                    if (isCredit) {
                        fromWallet.setBalance(fromWallet.getBalance() + amount);
                        transactionRepository.save(new Transaction(fromWallet, "CREDIT", amount, timestamp, "Bank load"));
                    } else {
                        fromWallet.setBalance(fromWallet.getBalance() - amount);
                        transactionRepository.save(new Transaction(fromWallet, "DEBIT", amount, timestamp, "Withdrawal"));
                    }
                }

                walletRepository.save(fromWallet);
                walletRepository.save(toWallet);
            }

            System.out.println("20 realistic transactions loaded!");
        }

        System.out.println("Demo data loaded successfully!");
    }
}
