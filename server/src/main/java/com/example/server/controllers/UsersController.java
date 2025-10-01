package com.example.server.controllers;

import com.example.server.models.User;
import com.example.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    // Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Create a new user
    @PostMapping
    public User createUser(@RequestBody User user) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        String timeNow = java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"));

        long countToday = userRepository.count();

        String sequence = String.format("%02d", countToday + 1);

        String accountNumber = today + "-" + timeNow + "-" + sequence;
        user.setAccount_number(accountNumber);

        if (user.getTin_number() == null || user.getTin_number().isEmpty()) {
            user.setTin_number("0000");
        }

        return userRepository.save(user);
    }

    // Update an existing user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Update fields
            user.setFirstname(userDetails.getFirstname());
            user.setLastname(userDetails.getLastname());
            user.setMiddlename(userDetails.getMiddlename());
            user.setSuffix(userDetails.getSuffix());
            user.setBlk_room(userDetails.getBlk_room());
            user.setBuilding(userDetails.getBuilding());
            user.setStreet(userDetails.getStreet());
            user.setBarangay(userDetails.getBarangay());
            user.setProvince(userDetails.getProvince());
            user.setZip_code(userDetails.getZip_code());
            user.setContact_no(userDetails.getContact_no());
            user.setTel_no(userDetails.getTel_no());
            user.setEmail(userDetails.getEmail());
            user.setValid_id_number(userDetails.getValid_id_number());
            user.setValid_id_type(userDetails.getValid_id_type());

            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
