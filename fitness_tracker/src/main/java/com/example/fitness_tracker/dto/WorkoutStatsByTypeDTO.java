package com.example.fitness_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutStatsByTypeDTO {
    private String type;
    private Long count;
    private Integer totalDuration;
    private Integer totalCaloriesBurned;
    private Double avgDuration;
    private Double avgCaloriesBurned;
} 