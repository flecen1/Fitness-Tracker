package com.example.fitness_tracker.controller;

import com.example.fitness_tracker.dto.WorkoutRequest;
import com.example.fitness_tracker.model.User;
import com.example.fitness_tracker.model.Workout;
import com.example.fitness_tracker.service.UserService;
import com.example.fitness_tracker.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/app/workouts")
public class WorkoutWebController {

    private final WorkoutService workoutService;
    private final UserService userService;

    @Autowired
    public WorkoutWebController(WorkoutService workoutService, UserService userService) {
        this.workoutService = workoutService;
        this.userService = userService;
    }

    @GetMapping
    public String listWorkouts(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        List<Workout> workouts = workoutService.getWorkoutsByUserId(user.getId());
        model.addAttribute("workouts", workouts);
        return "workouts/list";
    }

    @GetMapping("/create")
    public String createWorkoutForm(Model model) {
        model.addAttribute("workoutRequest", new WorkoutRequest());
        model.addAttribute("types", List.of("Кардио", "Силовая", "Йога", "Пилатес", "Растяжка", "Другое"));
        model.addAttribute("activePage", "workouts");
        return "workouts/create";
    }

    @PostMapping("/create")
    public String createWorkout(
            @Valid @ModelAttribute("workoutRequest") WorkoutRequest workoutRequest,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("types", List.of("Кардио", "Силовая", "Йога", "Пилатес", "Растяжка", "Другое"));
            return "workouts/create";
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Workout workout = Workout.builder()
                .name(workoutRequest.getName())
                .type(workoutRequest.getType())
                .duration(workoutRequest.getDuration())
                .caloriesBurned(workoutRequest.getCaloriesBurned())
                .description(workoutRequest.getDescription())
                .completedAt(workoutRequest.getCompletedAt())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        workoutService.createWorkout(workout);
        redirectAttributes.addFlashAttribute("successMessage", "Тренировка успешно добавлена!");
        return "redirect:/app/workouts";
    }

    @GetMapping("/{id}")
    public String viewWorkout(@PathVariable Long id, Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        Optional<Workout> workoutOpt = workoutService.getWorkoutById(id);
        
        if (workoutOpt.isEmpty() || !workoutOpt.get().getUser().getId().equals(user.getId())) {
            return "redirect:/app/workouts";
        }
        
        model.addAttribute("workout", workoutOpt.get());
        return "workouts/view";
    }

    @GetMapping("/{id}/edit")
    public String editWorkoutForm(@PathVariable Long id, Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        Optional<Workout> workoutOpt = workoutService.getWorkoutById(id);
        
        if (workoutOpt.isEmpty() || !workoutOpt.get().getUser().getId().equals(user.getId())) {
            return "redirect:/app/workouts";
        }
        
        Workout workout = workoutOpt.get();
        
        WorkoutRequest workoutRequest = WorkoutRequest.builder()
                .name(workout.getName())
                .type(workout.getType())
                .duration(workout.getDuration())
                .caloriesBurned(workout.getCaloriesBurned())
                .description(workout.getDescription())
                .completedAt(workout.getCompletedAt())
                .build();
        
        model.addAttribute("workoutRequest", workoutRequest);
        model.addAttribute("workoutId", id);
        model.addAttribute("types", List.of("Кардио", "Силовая", "Йога", "Пилатес", "Растяжка", "Другое"));
        return "workouts/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateWorkout(
            @PathVariable Long id,
            @Valid @ModelAttribute("workoutRequest") WorkoutRequest workoutRequest,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("workoutId", id);
            model.addAttribute("types", List.of("Кардио", "Силовая", "Йога", "Пилатес", "Растяжка", "Другое"));
            return "workouts/edit";
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        Optional<Workout> workoutOpt = workoutService.getWorkoutById(id);
        
        if (workoutOpt.isEmpty() || !workoutOpt.get().getUser().getId().equals(user.getId())) {
            return "redirect:/app/workouts";
        }
        
        Workout workout = workoutOpt.get();
        workout.setName(workoutRequest.getName());
        workout.setType(workoutRequest.getType());
        workout.setDuration(workoutRequest.getDuration());
        workout.setCaloriesBurned(workoutRequest.getCaloriesBurned());
        workout.setDescription(workoutRequest.getDescription());
        workout.setCompletedAt(workoutRequest.getCompletedAt());
        
        workoutService.updateWorkout(id, workout);
        redirectAttributes.addFlashAttribute("successMessage", "Тренировка успешно обновлена!");
        return "redirect:/app/workouts";
    }

    @PostMapping("/{id}/delete")
    public String deleteWorkout(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        Optional<Workout> workoutOpt = workoutService.getWorkoutById(id);
        
        if (workoutOpt.isPresent() && workoutOpt.get().getUser().getId().equals(user.getId())) {
            workoutService.deleteWorkout(id);
            redirectAttributes.addFlashAttribute("successMessage", "Тренировка успешно удалена!");
        }
        
        return "redirect:/app/workouts";
    }
} 