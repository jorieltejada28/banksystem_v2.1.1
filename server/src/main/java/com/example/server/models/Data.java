package com.example.server.models;

import jakarta.persistence.*;

@Entity
@Table(name = "data")
public class Data {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "pin_number", nullable = false)
    private String pinNumber;

    @Column(name = "status")
    private String status;

    @Column(name = "balance")
    private Double balance;

    // Foreign key: user_id → users.id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    // Constructors
    public Data() {}

    public Data(String fullName, String accountNumber, String pinNumber, String status, Double balance, User user) {
        this.fullName = fullName;
        this.accountNumber = accountNumber;
        this.pinNumber = pinNumber;
        this.status = status;
        this.balance = balance;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
