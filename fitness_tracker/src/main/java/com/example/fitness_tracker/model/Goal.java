package com.example.fitness_tracker.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String type; // WEIGHT_LOSS, MUSCLE_GAIN, ENDURANCE, etc.
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private Integer targetValue; // целевое значение (например, потеря веса в кг)
    
    private Integer currentValue; // текущий прогресс
    
    private String unit; // единица измерения (кг, км, часы и т.д.)
    
    @Column(nullable = false)
    private LocalDate targetDate; // дата достижения цели
    
    @Column(nullable = false)
    private Boolean completed; // достигнута ли цель
    
    private LocalDateTime completedAt; // когда цель была достигнута
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
} 