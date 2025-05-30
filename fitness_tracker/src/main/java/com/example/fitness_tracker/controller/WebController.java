package com.example.fitness_tracker.controller;

import com.example.fitness_tracker.model.Goal;
import com.example.fitness_tracker.model.User;
import com.example.fitness_tracker.model.Workout;
import com.example.fitness_tracker.service.GoalService;
import com.example.fitness_tracker.service.UserService;
import com.example.fitness_tracker.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Controller
public class WebController {

    private final WorkoutService workoutService;
    private final GoalService goalService;
    private final UserService userService;

    @Autowired
    public WebController(WorkoutService workoutService, GoalService goalService, UserService userService) {
        this.workoutService = workoutService;
        this.goalService = goalService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/verification")
    public String verification() {
        return "verification";
    }

    // Страницы для авторизованных пользователей
    @GetMapping("/app/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        // Получаем список тренировок пользователя
        List<Workout> workouts = workoutService.getWorkoutsByUserId(user.getId());
        
        // Получаем список целей пользователя
        List<Goal> goals = goalService.getGoalsByUserId(user.getId());
        
        // Получаем последние тренировки (максимум 2), фильтруя тренировки с null completedAt
        List<Workout> recentWorkouts = workouts.stream()
                .filter(workout -> workout.getCompletedAt() != null)
                .sorted((w1, w2) -> w2.getCompletedAt().compareTo(w1.getCompletedAt()))
                .limit(2)
                .toList();
        
        // Получаем активные цели (не завершенные) с непустыми полями targetValue и currentValue
        List<Goal> activeGoals = goals.stream()
                .filter(goal -> !goal.getCompleted())
                .filter(goal -> goal.getTargetValue() != null && goal.getTargetValue() > 0)
                .filter(goal -> goal.getCurrentValue() != null)
                .limit(2)
                .toList();
        
        // Общее количество часов тренировок
        double totalHours = workouts.stream()
                .mapToDouble(Workout::getDuration)
                .sum() / 60.0; // Преобразуем минуты в часы
        
        // Форматирование часов в "ч мин" формате
        int hours = (int) totalHours;
        int minutes = (int) Math.round((totalHours - hours) * 60);
        String formattedTime;
        
        if (hours > 0 && minutes > 0) {
            formattedTime = String.format(Locale.getDefault(), "%d ч %d мин", hours, minutes);
        } else if (hours > 0) {
            formattedTime = String.format(Locale.getDefault(), "%d ч", hours);
        } else {
            formattedTime = String.format(Locale.getDefault(), "%d мин", minutes);
        }
        
        model.addAttribute("workoutsCount", workouts.size());
        model.addAttribute("goalsCount", goals.size());
        model.addAttribute("totalHours", formattedTime);
        model.addAttribute("recentWorkouts", recentWorkouts);
        model.addAttribute("activeGoals", activeGoals);
        
        return "app/dashboard";
    }

    @GetMapping("/app/profile")
    public String profile() {
        return "app/profile";
    }
} 