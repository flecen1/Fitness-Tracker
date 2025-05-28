package com.example.fitness_tracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRequest {
    
    @NotBlank(message = "Workout name is required")
    private String name;
    
    @NotBlank(message = "Workout type is required")
    private String type;
    
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be greater than 0")
    private Integer duration;
    
    @NotNull(message = "Calories burned is required")
    @Min(value = 0, message = "Calories burned must be positive")
    private Integer caloriesBurned;
    
    private String description;
    
    private LocalDateTime completedAt;
} 