package com.example.fitness_tracker.repository;

import com.example.fitness_tracker.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    
    List<Goal> findByUserId(Long userId);
    
    List<Goal> findByType(String type);
    
    List<Goal> findByUserIdAndType(Long userId, String type);
    
    List<Goal> findByTargetDateBetween(LocalDate start, LocalDate end);
    
    List<Goal> findByUserIdAndTargetDateBetween(Long userId, LocalDate start, LocalDate end);
    
    List<Goal> findByCompleted(Boolean completed);
    
    List<Goal> findByUserIdAndCompleted(Long userId, Boolean completed);
    
    List<Goal> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<Goal> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
} 