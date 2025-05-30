package com.example.fitness_tracker.controller;

import com.example.fitness_tracker.dto.CaloriesProgressDTO;
import com.example.fitness_tracker.dto.WorkoutStatsByTypeDTO;
import com.example.fitness_tracker.exception.ResourceNotFoundException;
import com.example.fitness_tracker.model.User;
import com.example.fitness_tracker.service.StatsService;
import com.example.fitness_tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/app/stats")
public class StatsWebController {

    private final StatsService statsService;
    private final UserService userService;
    
    @Autowired
    public StatsWebController(StatsService statsService, UserService userService) {
        this.statsService = statsService;
        this.userService = userService;
    }
    
    @GetMapping
    public String showStats(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
        
        // Получаем статистику по типам тренировок
        List<WorkoutStatsByTypeDTO> workoutStats = statsService.getWorkoutStatsByType(user.getId());
        model.addAttribute("workoutStats", workoutStats);
        
        // Получаем статистику по калориям за последние 30 дней
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        
        List<CaloriesProgressDTO> caloriesProgress = statsService.getCaloriesProgress(
                user.getId(), startDate, endDate, "day");
        model.addAttribute("caloriesProgress", caloriesProgress);
        
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("period", "day");
        
        return "stats/dashboard";
    }
    
    @GetMapping("/calories-progress")
    public String showCaloriesProgress(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "day") String period,
            Model model) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
        
        // Если даты не указаны, используем последние 30 дней
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        List<CaloriesProgressDTO> caloriesProgress = statsService.getCaloriesProgress(
                user.getId(), startDate, endDate, period);
        
        model.addAttribute("caloriesProgress", caloriesProgress);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("period", period);
        
        return "stats/calories-progress";
    }
    
    @GetMapping("/workout-stats")
    public String showWorkoutStats(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
        
        List<WorkoutStatsByTypeDTO> workoutStats = statsService.getWorkoutStatsByType(user.getId());
        model.addAttribute("workoutStats", workoutStats);
        
        return "stats/workout-stats";
    }
} 