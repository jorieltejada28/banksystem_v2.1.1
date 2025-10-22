package com.example.server.repositories;

import com.example.server.models.Data;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataRepository extends JpaRepository<Data, Long> {
    Data findByAccountNumber(String accountNumber);
}
