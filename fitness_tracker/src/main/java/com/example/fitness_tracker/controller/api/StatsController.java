package com.example.fitness_tracker.controller.api;

import com.example.fitness_tracker.dto.CaloriesProgressDTO;
import com.example.fitness_tracker.dto.WorkoutStatsByTypeDTO;
import com.example.fitness_tracker.model.User;
import com.example.fitness_tracker.service.StatsService;
import com.example.fitness_tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    
    private final StatsService statsService;
    private final UserService userService;
    
    @Autowired
    public StatsController(StatsService statsService, UserService userService) {
        this.statsService = statsService;
        this.userService = userService;
    }
    
    @GetMapping("/workouts/by-type")
    public ResponseEntity<List<WorkoutStatsByTypeDTO>> getWorkoutStatsByType() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName()).orElseThrow(() -> 
                new RuntimeException("User not found"));
        
        List<WorkoutStatsByTypeDTO> stats = statsService.getWorkoutStatsByType(user.getId());
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/progress/calories")
    public ResponseEntity<List<CaloriesProgressDTO>> getCaloriesProgress(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "day") String period) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName()).orElseThrow(() -> 
                new RuntimeException("User not found"));
        
        // Если даты не указаны, используем последние 30 дней
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        List<CaloriesProgressDTO> progress = statsService.getCaloriesProgress(
                user.getId(), startDate, endDate, period);
        
        return ResponseEntity.ok(progress);
    }
} 