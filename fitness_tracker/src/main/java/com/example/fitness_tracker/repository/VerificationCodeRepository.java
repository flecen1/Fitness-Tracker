package com.example.fitness_tracker.repository;

import com.example.fitness_tracker.model.User;
import com.example.fitness_tracker.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findFirstByUserOrderByCreatedAtDesc(User user);
    void deleteAllByUser(User user);
} 