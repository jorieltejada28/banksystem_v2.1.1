package com.example.server.controllers;

import com.example.server.models.User;
import com.example.server.models.Data;
import com.example.server.repositories.DataRepository;
import com.example.server.repositories.UserRepository;
import com.example.server.utils.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataRepository dataRepository;

    private Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();

    // Create a new user
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User userRequest) {
        try {
            // 1. Save user first
            User savedUser = userRepository.save(userRequest);

            // 2. Create Data record linked to that user
            Data data = new Data();
            data.setUser(savedUser);

            // Build full name safely
            String first = savedUser.getFirstname() != null ? savedUser.getFirstname() : "";
            String middle = (savedUser.getMiddlename() != null && !savedUser.getMiddlename().isEmpty())
                    ? " " + savedUser.getMiddlename()
                    : "";
            String last = savedUser.getLastname() != null ? " " + savedUser.getLastname() : "";

            String fullName = (first + middle + last).trim();
            data.setFullName(fullName.isEmpty() ? "Unnamed User" : fullName);

            // Generate account number (ddMMyy-HHmmss-###)
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
            String timeNow = java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
            long countToday = dataRepository.count();
            String sequence = String.format("%03d", countToday + 1);

            data.setAccountNumber(today + "-" + timeNow + "-" + sequence);

            // Defaults
            data.setPinNumber("0000");
            data.setStatus("Active");
            data.setBalance(0.0);

            // 3. Save Data record
            Data savedData = dataRepository.save(data);

            return ResponseEntity.ok(savedData);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error during signup: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Data loginRequest) {
        try {
            String accountNumber = loginRequest.getAccountNumber();
            String pinNumber = loginRequest.getPinNumber();

            // 1️ Validate input
            if (accountNumber == null || accountNumber.isEmpty() ||
                    pinNumber == null || pinNumber.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Account number and PIN are required.");
                return ResponseEntity.badRequest().body(response);
            }

            // 2️ Check if account exists
            Data existingData = dataRepository.findByAccountNumber(accountNumber);
            if (existingData == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Account not found.");
                return ResponseEntity.status(404).body(response);
            }

            // 3️ Validate PIN
            if (!existingData.getPinNumber().equals(pinNumber)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Incorrect PIN. Please try again.");
                return ResponseEntity.status(401).body(response);
            }

            // 4️ Check account status
            if (!"Active".equalsIgnoreCase(existingData.getStatus())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message",
                        "Something went wrong with your account. Please contact customer service for assistance.");
                return ResponseEntity.status(403).body(response);
            }

            // 5️ Generate JWT token
            String token = JwtUtil.generateToken(existingData.getAccountNumber());

            // 6️ Create success response
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", true);
            responseBody.put("message", "Login successful.");
            responseBody.put("token", token);
            responseBody.put("accountNumber", existingData.getAccountNumber());
            responseBody.put("fullName", existingData.getFullName());
            responseBody.put("status", existingData.getStatus());

            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error during login: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {

        Map<String, Object> response = new HashMap<>();

        // 1️ Check Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("success", false);
            response.put("message", "Missing or invalid Authorization header.");
            return ResponseEntity.badRequest().body(response);
        }

        // 2️ Extract token
        String token = authHeader.substring(7).trim();

        // 3️ Validate token
        if (!JwtUtil.isTokenValid(token)) {
            response.put("success", false);
            response.put("message", "Token is invalid or already expired.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // 4️ Invalidate (blacklist) token
        invalidatedTokens.add(token);

        // 5️ Add timestamp for log clarity
        String formattedDateTime = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy h:mm a"));

        // 6️ Success response
        response.put("success", true);
        response.put("message", "Logged out successfully. Session expired.");
        response.put("timestamp", formattedDateTime);

        return ResponseEntity.ok(response);
    }

    /**
     * Utility method to check token validity
     */
    public boolean isTokenValidAndActive(String token) {
        return JwtUtil.isTokenValid(token) && !invalidatedTokens.contains(token);
    }
}
