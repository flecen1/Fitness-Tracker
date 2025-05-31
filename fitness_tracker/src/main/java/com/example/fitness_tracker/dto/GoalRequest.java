package com.example.fitness_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
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
public class GoalRequest {
    
    @NotBlank(message = "Название цели не может быть пустым")
    private String name;
    
    @NotBlank(message = "Тип цели не может быть пустым")
    private String type;
    
    private String description;
    
    @NotNull(message = "Целевое значение не может быть пустым")
    @Min(value = 0, message = "Целевое значение не может быть отрицательным")
    private Integer targetValue;
    
    @Min(value = 0, message = "Текущее значение не может быть отрицательным")
    private Integer currentValue;
    
    private String unit;
    
    @NotNull(message = "Целевая дата не может быть пустой")
    @FutureOrPresent(message = "Целевая дата должна быть в настоящем или будущем")
    private LocalDate targetDate;
    
    private Boolean completed;
    
    private LocalDateTime completedAt;
} 