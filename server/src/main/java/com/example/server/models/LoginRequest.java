package com.example.server.models;

public class LoginRequest {
    private String accountNumber;
    private String pinNumber;

    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPinNumber() {
        return pinNumber;
    }
    public void setPinNumber(String pinNumber) {
        this.pinNumber = pinNumber;
    }
}
