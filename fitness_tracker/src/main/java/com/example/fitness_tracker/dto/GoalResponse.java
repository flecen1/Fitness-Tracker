package com.example.fitness_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponse {
    private Long id;
    private String name;
    private String type;
    private String description;
    private Integer targetValue;
    private Integer currentValue;
    private String unit;
    private LocalDate targetDate;
    private Boolean completed;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private Long userId;
    private String username;
    
    // Дополнительное поле для расчета процента выполнения
    private Integer progressPercentage;
} 