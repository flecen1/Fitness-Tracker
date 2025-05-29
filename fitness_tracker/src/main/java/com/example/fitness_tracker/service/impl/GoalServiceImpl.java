package com.example.fitness_tracker.service.impl;

import com.example.fitness_tracker.exception.ResourceNotFoundException;
import com.example.fitness_tracker.model.Goal;
import com.example.fitness_tracker.repository.GoalRepository;
import com.example.fitness_tracker.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    
    @Autowired
    public GoalServiceImpl(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }
    
    @Override
    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }
    
    @Override
    public List<Goal> getGoalsByUserId(Long userId) {
        return goalRepository.findByUserId(userId);
    }
    
    @Override
    public List<Goal> getGoalsByType(String type) {
        return goalRepository.findByType(type);
    }
    
    @Override
    public List<Goal> getGoalsByUserIdAndType(Long userId, String type) {
        return goalRepository.findByUserIdAndType(userId, type);
    }
    
    @Override
    public List<Goal> getGoalsByTargetDateBetween(LocalDate start, LocalDate end) {
        return goalRepository.findByTargetDateBetween(start, end);
    }
    
    @Override
    public List<Goal> getGoalsByUserIdAndTargetDateBetween(Long userId, LocalDate start, LocalDate end) {
        return goalRepository.findByUserIdAndTargetDateBetween(userId, start, end);
    }
    
    @Override
    public List<Goal> getGoalsByCompleted(Boolean completed) {
        return goalRepository.findByCompleted(completed);
    }
    
    @Override
    public List<Goal> getGoalsByUserIdAndCompleted(Long userId, Boolean completed) {
        return goalRepository.findByUserIdAndCompleted(userId, completed);
    }
    
    @Override
    public List<Goal> getGoalsByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return goalRepository.findByCreatedAtBetween(start, end);
    }
    
    @Override
    public List<Goal> getGoalsByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end) {
        return goalRepository.findByUserIdAndCreatedAtBetween(userId, start, end);
    }
    
    @Override
    public Optional<Goal> getGoalById(Long id) {
        return goalRepository.findById(id);
    }
    
    @Override
    public Goal createGoal(Goal goal) {
        // Устанавливаем текущую дату создания
        goal.setCreatedAt(LocalDateTime.now());
        
        // Установка значений по умолчанию для непредоставленных полей
        if (goal.getCompleted() == null) {
            goal.setCompleted(false);
        }
        
        if (goal.getCurrentValue() == null) {
            goal.setCurrentValue(0);
        }
        
        // Автоматически отмечаем цель как выполненную, если текущее значение достигло или превысило целевое
        if (Boolean.TRUE.equals(goal.getCompleted()) && goal.getCompletedAt() == null) {
            goal.setCompletedAt(LocalDateTime.now());
        } else if (Boolean.FALSE.equals(goal.getCompleted())) {
            goal.setCompletedAt(null);
        }
        
        // Проверка, достигнуто ли целевое значение
        if (goal.getTargetValue() != null && goal.getCurrentValue() != null && 
                goal.getCurrentValue() >= goal.getTargetValue() && !Boolean.TRUE.equals(goal.getCompleted())) {
            goal.setCompleted(true);
            goal.setCompletedAt(LocalDateTime.now());
        }
        
        return goalRepository.save(goal);
    }
    
    @Override
    public Goal updateGoal(Long id, Goal goalDetails) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        
        goal.setName(goalDetails.getName());
        goal.setType(goalDetails.getType());
        goal.setDescription(goalDetails.getDescription());
        goal.setTargetValue(goalDetails.getTargetValue());
        goal.setCurrentValue(goalDetails.getCurrentValue());
        goal.setUnit(goalDetails.getUnit());
        goal.setTargetDate(goalDetails.getTargetDate());
        
        // Обновляем статус завершения если он изменился
        if (Boolean.TRUE.equals(goalDetails.getCompleted()) && !Boolean.TRUE.equals(goal.getCompleted())) {
            goal.setCompleted(true);
            goal.setCompletedAt(LocalDateTime.now());
        } else if (Boolean.FALSE.equals(goalDetails.getCompleted()) && Boolean.TRUE.equals(goal.getCompleted())) {
            goal.setCompleted(false);
            goal.setCompletedAt(null);
        }
        
        return goalRepository.save(goal);
    }
    
    @Override
    public void deleteGoal(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        
        goalRepository.delete(goal);
    }
    
    @Override
    public Goal updateProgress(Long id, Integer currentValue) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        
        goal.setCurrentValue(currentValue);
        
        // Автоматически отмечаем цель как выполненную, если текущее значение достигло или превысило целевое
        if (goal.getTargetValue() != null && currentValue != null && 
                currentValue >= goal.getTargetValue() && !Boolean.TRUE.equals(goal.getCompleted())) {
            goal.setCompleted(true);
            goal.setCompletedAt(LocalDateTime.now());
        }
        
        return goalRepository.save(goal);
    }
    
    @Override
    public Goal markAsCompleted(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        
        goal.setCompleted(true);
        goal.setCompletedAt(LocalDateTime.now());
        
        return goalRepository.save(goal);
    }
} 