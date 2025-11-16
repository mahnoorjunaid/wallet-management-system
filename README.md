# 💼 Wallet Management System Backend

## Project Overview

This is a **Spring Boot backend** for managing users, wallets, transactions, and payments. It provides full CRUD operations, transaction history, spending limits, dispute management, and admin functionalities.

---

## ✨ Features

* **User Management**: Registration, profile update, password reset, 2FA setup
* **Wallet Management**: Create wallet, fund loading, peer-to-peer transfers, merchant payments, withdrawals
* **Transactions**: History retrieval, monthly statements, dispute handling
* **Admin Functionalities**: Activate/deactivate users and wallets, reset passwords, delete users/wallets
* **Validation**: Spending limits, sufficient balance checks

---

## 🛠️ Technologies Used

* Java 17
* Spring Boot
* H2 Database (or configured DB)
* Maven
* Git & GitHub

---

## ⚙️ Prerequisites

* JDK 17 installed
* Maven installed
* Git installed
* Postman (or any API testing tool)

---

## 🚀 Setup & Installation

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/mahnoorjunaid/wallet-management-system.git](https://github.com/mahnoorjunaid/wallet-management-system.git)
    ```
2.  **Navigate to the project directory**
    ```bash
    cd wallet-management-system
    ```
3.  **Build the project**
    ```bash
    mvn clean install
    ```
4.  **Run the backend**
    ```bash
    mvn spring-boot:run
    ```

### Access API

The backend runs on:
**`http://localhost:8080`**

You can test endpoints using Postman or any similar tool.

---

## 🛣️ API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/users` | Create a new user (Register) |
| `GET` | `/users/{id}` | Retrieve user details by ID |
| `PUT` | `/users/{id}` | Update user details |
| `POST` | `/users/{id}/reset-password` | Reset user password |
| `PATCH` | `/users/{id}/activate` or `/deactivate` | Admin user activation/deactivation |
| `DELETE` | `/users/{userId}` | Admin delete a user |
| `POST` | `/wallets` | Create a wallet for a user |
| `GET` | `/wallets/{userId}` | Retrieve wallet by user ID |
| `POST` | `/wallets/{userId}/fund` | Fund wallet via bank transfer |
| `POST` | `/wallets/transfer` | Peer-to-peer transfer |
| `POST` | `/wallets/{userId}/payQR` | Merchant payment |
| `POST` | `/wallets/{userId}/withdraw` | Withdraw funds from wallet |
| `PATCH` | `/wallets/{userId}/limit` | Set a spending limit |
| `DELETE` | `/wallets/{walletId}` | Admin delete a wallet |
| `GET` | `/transactions/{walletId}` | List all wallet transactions |
| `POST` | `/transactions/{id}/dispute` | Mark transaction as disputed |

> ⚠️ **Note:** Authentication and authorization are not fully implemented in this version. Admin APIs are currently accessible directly.

---

## 💾 Database

* **Database Type:** H2 in-memory database is used for testing.
* **Tables:** `users`, `wallets`, `transactions`
* **Relationships:**
    * One `User` → One `Wallet`
    * One `Wallet` → Many `Transactions`

### Demo Data

The project includes a `DataLoader` class that automatically populates the database on startup with:

* 10 users
* 10 wallets with random balances
* 20 transactions

This is only for testing purposes and will be cleared upon restarting the application.

---

## 🌳 Project Structure
wallet-management-system
├── src
│   ├── main
│   │   ├── java/com/wallet/walletmanagement
│   │   │   ├── controller
│   │   │   ├── dto
│   │   │   ├── exception
│   │   │   ├── model
│   │   │   ├── repository
│   │   │   └── service
│   │   └── resources
│   │       └── application.properties
├── pom.xml
└── README.md
### Running Tests

* No unit tests currently included.
* Use Postman to test all endpoints as listed above.

---

## 📜 License

This project is licensed under the **MIT License**.
