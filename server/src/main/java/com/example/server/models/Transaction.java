package com.example.server.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long dataId;

    private String transactionNumber;
    private Double withdraw;
    private Double charge;

    private LocalDateTime timestamp;

    public Transaction() {
    }

    public Transaction(Long id, Long userId, Long dataId, String transactionNumber, Double withdraw, Double charge,
            LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.dataId = dataId;
        this.transactionNumber = transactionNumber;
        this.withdraw = withdraw;
        this.charge = charge;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public Double getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(Double withdraw) {
        this.withdraw = withdraw;
    }

    public Double getCharge() {
        return charge;
    }

    public void setCharge(Double charge) {
        this.charge = charge;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // Optional: toString() for debugging
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", userId=" + userId +
                ", dataId=" + dataId +
                ", transactionNumber='" + transactionNumber + '\'' +
                ", withdraw=" + withdraw +
                ", charge=" + charge +
                ", timestamp=" + timestamp +
                '}';
    }
}
