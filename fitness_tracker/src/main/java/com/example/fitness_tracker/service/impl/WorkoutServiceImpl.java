package com.example.fitness_tracker.service.impl;

import com.example.fitness_tracker.exception.ResourceNotFoundException;
import com.example.fitness_tracker.model.Workout;
import com.example.fitness_tracker.repository.WorkoutRepository;
import com.example.fitness_tracker.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutRepository workoutRepository;

    @Autowired
    public WorkoutServiceImpl(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    @Override
    public List<Workout> getAllWorkouts() {
        return workoutRepository.findAll();
    }

    @Override
    public List<Workout> getWorkoutsByUserId(Long userId) {
        return workoutRepository.findByUserId(userId);
    }

    @Override
    public List<Workout> getWorkoutsByType(String type) {
        return workoutRepository.findByType(type);
    }

    @Override
    public List<Workout> getWorkoutsByUserIdAndType(Long userId, String type) {
        return workoutRepository.findByUserIdAndType(userId, type);
    }

    @Override
    public List<Workout> getWorkoutsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return workoutRepository.findByCreatedAtBetween(start, end);
    }

    @Override
    public List<Workout> getWorkoutsByUserIdBetweenDates(Long userId, LocalDateTime start, LocalDateTime end) {
        return workoutRepository.findByUserIdAndCreatedAtBetween(userId, start, end);
    }

    @Override
    public Optional<Workout> getWorkoutById(Long id) {
        return workoutRepository.findById(id);
    }

    @Override
    public Workout createWorkout(Workout workout) {
        workout.setCreatedAt(LocalDateTime.now());
        return workoutRepository.save(workout);
    }

    @Override
    public Workout updateWorkout(Long id, Workout workoutDetails) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + id));

        workout.setName(workoutDetails.getName());
        workout.setType(workoutDetails.getType());
        workout.setDuration(workoutDetails.getDuration());
        workout.setCaloriesBurned(workoutDetails.getCaloriesBurned());
        workout.setDescription(workoutDetails.getDescription());
        workout.setCompletedAt(workoutDetails.getCompletedAt());
        
        return workoutRepository.save(workout);
    }

    @Override
    public void deleteWorkout(Long id) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + id));
        
        workoutRepository.delete(workout);
    }
} 