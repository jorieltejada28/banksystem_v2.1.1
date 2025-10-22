package com.example.server.repositories;

import com.example.server.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Count transactions that occurred on/after `start` and before `end`
    long countByTimestampGreaterThanEqualAndTimestampLessThan(LocalDateTime start, LocalDateTime end);

}
