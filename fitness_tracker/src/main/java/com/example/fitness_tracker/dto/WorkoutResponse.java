package com.example.fitness_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutResponse {
    private Long id;
    private String name;
    private String type;
    private Integer duration;
    private Integer caloriesBurned;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private Long userId;
    private String username;
} 