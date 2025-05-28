package com.example.fitness_tracker.controller.api;

import com.example.fitness_tracker.dto.WorkoutRequest;
import com.example.fitness_tracker.dto.WorkoutResponse;
import com.example.fitness_tracker.exception.ResourceNotFoundException;
import com.example.fitness_tracker.model.User;
import com.example.fitness_tracker.model.Workout;
import com.example.fitness_tracker.service.UserService;
import com.example.fitness_tracker.service.WorkoutService;
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
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;
    private final UserService userService;

    @Autowired
    public WorkoutController(WorkoutService workoutService, UserService userService) {
        this.workoutService = workoutService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<WorkoutResponse>> getAllWorkouts(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<Workout> workouts;
        
        if (userId != null && type != null && startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            workouts = workoutService.getWorkoutsByUserIdAndType(userId, type)
                    .stream()
                    .filter(w -> !w.getCreatedAt().isBefore(startDateTime) && !w.getCreatedAt().isAfter(endDateTime))
                    .collect(Collectors.toList());
        } else if (userId != null && type != null) {
            workouts = workoutService.getWorkoutsByUserIdAndType(userId, type);
        } else if (userId != null && startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            workouts = workoutService.getWorkoutsByUserIdBetweenDates(userId, startDateTime, endDateTime);
        } else if (type != null && startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            workouts = workoutService.getWorkoutsByType(type)
                    .stream()
                    .filter(w -> !w.getCreatedAt().isBefore(startDateTime) && !w.getCreatedAt().isAfter(endDateTime))
                    .collect(Collectors.toList());
        } else if (userId != null) {
            workouts = workoutService.getWorkoutsByUserId(userId);
        } else if (type != null) {
            workouts = workoutService.getWorkoutsByType(type);
        } else if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            workouts = workoutService.getWorkoutsBetweenDates(startDateTime, endDateTime);
        } else {
            // If no filters, return all workouts
            workouts = workoutService.getAllWorkouts();
        }

        List<WorkoutResponse> workoutResponses = workouts.stream()
                .map(this::convertToWorkoutResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(workoutResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponse> getWorkoutById(@PathVariable Long id) {
        Workout workout = workoutService.getWorkoutById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout", "id", id));

        return ResponseEntity.ok(convertToWorkoutResponse(workout));
    }

    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(
            @Valid @RequestBody WorkoutRequest workoutRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", userDetails.getUsername()));

        Workout workout = Workout.builder()
                .name(workoutRequest.getName())
                .type(workoutRequest.getType())
                .duration(workoutRequest.getDuration())
                .caloriesBurned(workoutRequest.getCaloriesBurned())
                .description(workoutRequest.getDescription())
                .completedAt(workoutRequest.getCompletedAt())
                .user(user)
                .build();

        Workout createdWorkout = workoutService.createWorkout(workout);

        return new ResponseEntity<>(convertToWorkoutResponse(createdWorkout), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutResponse> updateWorkout(
            @PathVariable Long id,
            @Valid @RequestBody WorkoutRequest workoutRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Check if workout exists
        Workout existingWorkout = workoutService.getWorkoutById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout", "id", id));
        
        // Check if user owns this workout
        String username = userDetails.getUsername();
        if (!existingWorkout.getUser().getUsername().equals(username)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Update workout details without changing the user
        existingWorkout.setName(workoutRequest.getName());
        existingWorkout.setType(workoutRequest.getType());
        existingWorkout.setDuration(workoutRequest.getDuration());
        existingWorkout.setCaloriesBurned(workoutRequest.getCaloriesBurned());
        existingWorkout.setDescription(workoutRequest.getDescription());
        existingWorkout.setCompletedAt(workoutRequest.getCompletedAt());

        Workout updatedWorkout = workoutService.updateWorkout(id, existingWorkout);

        return ResponseEntity.ok(convertToWorkoutResponse(updatedWorkout));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Workout workout = workoutService.getWorkoutById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout", "id", id));
        
        // Check if user owns this workout
        String username = userDetails.getUsername();
        if (!workout.getUser().getUsername().equals(username)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        
        workoutService.deleteWorkout(id);
        
        return ResponseEntity.noContent().build();
    }
    
    private WorkoutResponse convertToWorkoutResponse(Workout workout) {
        return WorkoutResponse.builder()
                .id(workout.getId())
                .name(workout.getName())
                .type(workout.getType())
                .duration(workout.getDuration())
                .caloriesBurned(workout.getCaloriesBurned())
                .description(workout.getDescription())
                .createdAt(workout.getCreatedAt())
                .completedAt(workout.getCompletedAt())
                .userId(workout.getUser().getId())
                .username(workout.getUser().getUsername())
                .build();
    }
} 