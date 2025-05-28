package com.example.fitness_tracker.service;

import com.example.fitness_tracker.model.Workout;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkoutService {
    List<Workout> getAllWorkouts();
    List<Workout> getWorkoutsByUserId(Long userId);
    List<Workout> getWorkoutsByType(String type);
    List<Workout> getWorkoutsByUserIdAndType(Long userId, String type);
    List<Workout> getWorkoutsBetweenDates(LocalDateTime start, LocalDateTime end);
    List<Workout> getWorkoutsByUserIdBetweenDates(Long userId, LocalDateTime start, LocalDateTime end);
    Optional<Workout> getWorkoutById(Long id);
    Workout createWorkout(Workout workout);
    Workout updateWorkout(Long id, Workout workoutDetails);
    void deleteWorkout(Long id);
} 