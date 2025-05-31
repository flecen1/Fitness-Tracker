package com.example.fitness_tracker.controller.api;

import com.example.fitness_tracker.dto.GoalRequest;
import com.example.fitness_tracker.dto.GoalResponse;
import com.example.fitness_tracker.exception.ResourceNotFoundException;
import com.example.fitness_tracker.model.Goal;
import com.example.fitness_tracker.model.User;
import com.example.fitness_tracker.service.GoalService;
import com.example.fitness_tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;
    private final UserService userService;

    @Autowired
    public GoalController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> getAllGoals(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Boolean completed,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<Goal> goals;
        
        // Фильтрация по нескольким параметрам
        if (userId != null && type != null && startDate != null && endDate != null) {
            goals = goalService.getGoalsByUserIdAndType(userId, type)
                    .stream()
                    .filter(g -> !g.getTargetDate().isBefore(startDate) && !g.getTargetDate().isAfter(endDate))
                    .collect(Collectors.toList());
        } else if (userId != null && type != null) {
            goals = goalService.getGoalsByUserIdAndType(userId, type);
        } else if (userId != null && startDate != null && endDate != null) {
            goals = goalService.getGoalsByUserIdAndTargetDateBetween(userId, startDate, endDate);
        } else if (userId != null && completed != null) {
            goals = goalService.getGoalsByUserIdAndCompleted(userId, completed);
        } else if (type != null && startDate != null && endDate != null) {
            goals = goalService.getGoalsByType(type)
                    .stream()
                    .filter(g -> !g.getTargetDate().isBefore(startDate) && !g.getTargetDate().isAfter(endDate))
                    .collect(Collectors.toList());
        } else if (type != null && completed != null) {
            goals = goalService.getGoalsByType(type)
                    .stream()
                    .filter(g -> g.getCompleted() == completed)
                    .collect(Collectors.toList());
        } else if (startDate != null && endDate != null && completed != null) {
            goals = goalService.getGoalsByTargetDateBetween(startDate, endDate)
                    .stream()
                    .filter(g -> g.getCompleted() == completed)
                    .collect(Collectors.toList());
        } else if (userId != null) {
            goals = goalService.getGoalsByUserId(userId);
        } else if (type != null) {
            goals = goalService.getGoalsByType(type);
        } else if (startDate != null && endDate != null) {
            goals = goalService.getGoalsByTargetDateBetween(startDate, endDate);
        } else if (completed != null) {
            goals = goalService.getGoalsByCompleted(completed);
        } else {
            // Если нет фильтров, возвращаем все цели
            goals = goalService.getAllGoals();
        }

        List<GoalResponse> goalResponses = goals.stream()
                .map(this::convertToGoalResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(goalResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoalById(@PathVariable Long id) {
        Goal goal = goalService.getGoalById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));

        return ResponseEntity.ok(convertToGoalResponse(goal));
    }

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @Valid @RequestBody GoalRequest goalRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", userDetails.getUsername()));

        Goal goal = Goal.builder()
                .name(goalRequest.getName())
                .type(goalRequest.getType())
                .description(goalRequest.getDescription())
                .targetValue(goalRequest.getTargetValue())
                .currentValue(goalRequest.getCurrentValue())
                .unit(goalRequest.getUnit())
                .targetDate(goalRequest.getTargetDate())
                .completed(goalRequest.getCompleted() != null ? goalRequest.getCompleted() : false)
                .completedAt(goalRequest.getCompleted() != null && goalRequest.getCompleted() ? 
                        (goalRequest.getCompletedAt() != null ? goalRequest.getCompletedAt() : LocalDateTime.now()) : null)
                .user(user)
                .build();

        Goal createdGoal = goalService.createGoal(goal);

        return new ResponseEntity<>(convertToGoalResponse(createdGoal), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody GoalRequest goalRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Проверяем, существует ли цель
        Goal existingGoal = goalService.getGoalById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        
        // Проверяем, принадлежит ли цель текущему пользователю
        String username = userDetails.getUsername();
        if (!existingGoal.getUser().getUsername().equals(username)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Обновляем детали цели, не меняя пользователя
        existingGoal.setName(goalRequest.getName());
        existingGoal.setType(goalRequest.getType());
        existingGoal.setDescription(goalRequest.getDescription());
        existingGoal.setTargetValue(goalRequest.getTargetValue());
        existingGoal.setCurrentValue(goalRequest.getCurrentValue());
        existingGoal.setUnit(goalRequest.getUnit());
        existingGoal.setTargetDate(goalRequest.getTargetDate());
        
        // Обновляем статус завершения если он изменился
        if (Boolean.TRUE.equals(goalRequest.getCompleted()) && !Boolean.TRUE.equals(existingGoal.getCompleted())) {
            existingGoal.setCompleted(true);
            existingGoal.setCompletedAt(LocalDateTime.now());
        } else if (Boolean.FALSE.equals(goalRequest.getCompleted()) && Boolean.TRUE.equals(existingGoal.getCompleted())) {
            existingGoal.setCompleted(false);
            existingGoal.setCompletedAt(null);
        }

        Goal updatedGoal = goalService.updateGoal(id, existingGoal);

        return ResponseEntity.ok(convertToGoalResponse(updatedGoal));
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<GoalResponse> updateProgress(
            @PathVariable Long id,
            @RequestParam Integer currentValue,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Проверяем, что значение не отрицательное
        if (currentValue < 0) {
            return ResponseEntity.badRequest().build();
        }
        
        // Проверяем, существует ли цель
        Goal existingGoal = goalService.getGoalById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        
        // Проверяем, принадлежит ли цель текущему пользователю
        String username = userDetails.getUsername();
        if (!existingGoal.getUser().getUsername().equals(username)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        
        // Обновляем прогресс
        Goal updatedGoal = goalService.updateProgress(id, currentValue);
        
        return ResponseEntity.ok(convertToGoalResponse(updatedGoal));
    }
    
    @PatchMapping("/{id}/complete")
    public ResponseEntity<GoalResponse> markAsCompleted(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Проверяем, существует ли цель
        Goal existingGoal = goalService.getGoalById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        
        // Проверяем, принадлежит ли цель текущему пользователю
        String username = userDetails.getUsername();
        if (!existingGoal.getUser().getUsername().equals(username)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        
        // Отмечаем цель как выполненную
        Goal completedGoal = goalService.markAsCompleted(id);
        
        return ResponseEntity.ok(convertToGoalResponse(completedGoal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Goal goal = goalService.getGoalById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        
        // Проверяем, принадлежит ли цель текущему пользователю
        String username = userDetails.getUsername();
        if (!goal.getUser().getUsername().equals(username)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        
        goalService.deleteGoal(id);
        
        return ResponseEntity.noContent().build();
    }
    
    private GoalResponse convertToGoalResponse(Goal goal) {
        // Расчет процента выполнения, если есть целевое и текущее значение
        Integer progressPercentage = null;
        if (goal.getTargetValue() != null && goal.getCurrentValue() != null && goal.getTargetValue() > 0) {
            progressPercentage = Math.min(100, (int) (((double) goal.getCurrentValue() / goal.getTargetValue()) * 100));
        }
        
        return GoalResponse.builder()
                .id(goal.getId())
                .name(goal.getName())
                .type(goal.getType())
                .description(goal.getDescription())
                .targetValue(goal.getTargetValue())
                .currentValue(goal.getCurrentValue())
                .unit(goal.getUnit())
                .targetDate(goal.getTargetDate())
                .completed(goal.getCompleted())
                .completedAt(goal.getCompletedAt())
                .createdAt(goal.getCreatedAt())
                .userId(goal.getUser().getId())
                .username(goal.getUser().getUsername())
                .progressPercentage(progressPercentage)
                .build();
    }
} 