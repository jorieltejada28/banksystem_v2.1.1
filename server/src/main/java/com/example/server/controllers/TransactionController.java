package com.example.server.controllers;

import com.example.server.models.Data;
import com.example.server.repositories.DataRepository;
import com.example.server.utils.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.server.models.Transaction;
import com.example.server.repositories.TransactionRepository;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // CHECK BALANCE (Protected by JWT)
    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<?> getBalance(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String accountNumber) {

        Map<String, Object> response = new HashMap<>();

        // 1️ Check Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("success", false);
            response.put("message", "Missing or invalid Authorization header.");
            return ResponseEntity.status(401).body(response);
        }

        // 2 Extract the token
        String token = authHeader.substring(7);

        // 3️ Validate token
        if (!JwtUtil.isTokenValid(token)) {
            response.put("success", false);
            response.put("message", "Invalid or expired token.");
            return ResponseEntity.status(401).body(response);
        }

        // 4️ Extract account number (username) from token
        String tokenAccountNumber = JwtUtil.getUsernameFromToken(token);

        // 5️ Check if the token's account matches the requested one
        if (!tokenAccountNumber.equals(accountNumber)) {
            response.put("success", false);
            response.put("message", "Unauthorized access: account mismatch.");
            return ResponseEntity.status(403).body(response);
        }

        // 6️ Find account in database
        Data existingData = dataRepository.findByAccountNumber(accountNumber);
        if (existingData == null) {
            response.put("success", false);
            response.put("message", "Account not found.");
            return ResponseEntity.status(404).body(response);
        }

        // 7️ Format readable date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy h:mm a");
        String formattedDateTime = now.format(formatter);

        // 8️ Prepare data
        Map<String, Object> data = new HashMap<>();
        data.put("accountNumber", existingData.getAccountNumber());
        data.put("fullName", existingData.getFullName());
        data.put("balance", existingData.getBalance());
        data.put("status", existingData.getStatus());
        data.put("timestamp", formattedDateTime);

        // 9️ Return success response
        response.put("success", true);
        response.put("message", "Balance retrieved successfully.");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cashin/{accountNumber}")
    public ResponseEntity<?> cashIn(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String accountNumber,
            @RequestBody Map<String, Object> requestBody) {

        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("success", false);
            response.put("message", "Missing or invalid Authorization header.");
            return ResponseEntity.status(401).body(response);
        }

        String token = authHeader.substring(7);

        if (!JwtUtil.isTokenValid(token)) {
            response.put("success", false);
            response.put("message", "Invalid or expired token.");
            return ResponseEntity.status(401).body(response);
        }

        String tokenAccountNumber = JwtUtil.getUsernameFromToken(token);
        if (!tokenAccountNumber.equals(accountNumber)) {
            response.put("success", false);
            response.put("message", "Unauthorized access: account mismatch.");
            return ResponseEntity.status(403).body(response);
        }

        Data existingData = dataRepository.findByAccountNumber(accountNumber);
        if (existingData == null) {
            response.put("success", false);
            response.put("message", "Account not found.");
            return ResponseEntity.status(404).body(response);
        }

        Double amount;
        try {
            amount = Double.valueOf(requestBody.get("amount").toString());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Invalid amount provided.");
            return ResponseEntity.badRequest().body(response);
        }

        if (amount <= 0) {
            response.put("success", false);
            response.put("message", "Amount must be greater than zero.");
            return ResponseEntity.badRequest().body(response);
        }

        // Update balance
        existingData.setBalance(existingData.getBalance() + amount);
        dataRepository.save(existingData);

        // === Generate Transaction Number ===
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timePart = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        String milliPart = now.format(DateTimeFormatter.ofPattern("SSS"));

        // Count today's transactions using a time range
        LocalDate today = now.toLocalDate();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfNextDay = startOfDay.plusDays(1);

        long todayCount = transactionRepository
                .countByTimestampGreaterThanEqualAndTimestampLessThan(startOfDay, startOfNextDay);

        String counter = String.format("%02d", todayCount + 1);

        // New format: TXN-20251010-135959-123-01
        String transactionNumber = "TXN-" + datePart + "-" + timePart + "-" + milliPart + "-" + counter;

        // Log transaction
        Transaction transaction = new Transaction();
        transaction.setUserId(existingData.getId());
        transaction.setDataId(existingData.getId());
        transaction.setTransactionNumber(transactionNumber);
        transaction.setWithdraw(0.0);
        transaction.setCharge(0.0);
        transaction.setTimestamp(now);
        transactionRepository.save(transaction);

        String formattedDateTime = now.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy h:mm a"));

        Map<String, Object> data = new HashMap<>();
        data.put("accountNumber", existingData.getAccountNumber());
        data.put("fullName", existingData.getFullName());
        data.put("newBalance", existingData.getBalance());
        data.put("transactionNumber", transactionNumber);
        data.put("status", existingData.getStatus());
        data.put("timestamp", formattedDateTime);

        response.put("success", true);
        response.put("message", "Cash-in successful.");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}
