package com.wallet.walletmanagement.dto;

public class FundRequest {
    private Double amount;
    private String bankReference;

    // getters and setters
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getBankReference() { return bankReference; }
    public void setBankReference(String bankReference) { this.bankReference = bankReference; }
}
