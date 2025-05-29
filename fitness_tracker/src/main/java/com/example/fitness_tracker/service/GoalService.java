package com.example.fitness_tracker.service;

import com.example.fitness_tracker.model.Goal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GoalService {
    
    List<Goal> getAllGoals();
    
    List<Goal> getGoalsByUserId(Long userId);
    
    List<Goal> getGoalsByType(String type);
    
    List<Goal> getGoalsByUserIdAndType(Long userId, String type);
    
    List<Goal> getGoalsByTargetDateBetween(LocalDate start, LocalDate end);
    
    List<Goal> getGoalsByUserIdAndTargetDateBetween(Long userId, LocalDate start, LocalDate end);
    
    List<Goal> getGoalsByCompleted(Boolean completed);
    
    List<Goal> getGoalsByUserIdAndCompleted(Long userId, Boolean completed);
    
    List<Goal> getGoalsByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<Goal> getGoalsByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
    
    Optional<Goal> getGoalById(Long id);
    
    Goal createGoal(Goal goal);
    
    Goal updateGoal(Long id, Goal goalDetails);
    
    void deleteGoal(Long id);
    
    // Дополнительные методы для работы с прогрессом
    Goal updateProgress(Long id, Integer currentValue);
    
    Goal markAsCompleted(Long id);
} 