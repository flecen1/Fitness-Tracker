package com.example.fitness_tracker.repository;

import com.example.fitness_tracker.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByUserId(Long userId);
    List<Workout> findByType(String type);
    List<Workout> findByUserIdAndType(Long userId, String type);
    List<Workout> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Workout> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
} 