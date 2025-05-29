package com.example.fitness_tracker.controller;

import com.example.fitness_tracker.dto.GoalRequest;
import com.example.fitness_tracker.exception.ResourceNotFoundException;
import com.example.fitness_tracker.model.Goal;
import com.example.fitness_tracker.model.User;
import com.example.fitness_tracker.service.GoalService;
import com.example.fitness_tracker.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/app/goals")
public class GoalWebController {

    private static final Logger logger = LoggerFactory.getLogger(GoalWebController.class);
    
    private final GoalService goalService;
    private final UserService userService;
    
    // Список типов целей для выбора в формах
    private static final List<String> GOAL_TYPES = Arrays.asList(
            "WEIGHT_LOSS", "MUSCLE_GAIN", "ENDURANCE", "STRENGTH", 
            "FLEXIBILITY", "CARDIO", "STEPS", "WATER_INTAKE", "OTHER"
    );

    @Autowired
    public GoalWebController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    @GetMapping
    public String listGoals(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String completed,
            Model model) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
        
        // Преобразуем строковый параметр в Boolean, если он указан
        final Boolean completedBool = (completed != null && !completed.isEmpty()) 
                ? Boolean.valueOf(completed) 
                : null;
        
        List<Goal> goals;
        
        // Логирование параметров фильтрации
        logger.info("Filtering goals - type: {}, completed: {}", type, completedBool);
        
        // Получаем все цели пользователя для сравнения
        List<Goal> allUserGoals = goalService.getGoalsByUserId(user.getId());
        logger.info("Total user goals: {}", allUserGoals.size());
        
        if (type != null && !type.isEmpty() && completedBool != null) {
            goals = goalService.getGoalsByUserIdAndType(user.getId(), type)
                    .stream()
                    .filter(g -> g.getCompleted().equals(completedBool))
                    .toList();
            logger.info("Filtered by type and completed: {}", goals.size());
        } else if (type != null && !type.isEmpty()) {
            goals = goalService.getGoalsByUserIdAndType(user.getId(), type);
            logger.info("Filtered by type: {}", goals.size());
        } else if (completedBool != null) {
            goals = goalService.getGoalsByUserIdAndCompleted(user.getId(), completedBool);
            logger.info("Filtered by completed: {}", goals.size());
        } else {
            goals = allUserGoals;
            logger.info("No filtering applied");
        }
        
        model.addAttribute("goals", goals);
        model.addAttribute("types", GOAL_TYPES);
        
        return "goals/list";
    }

    @GetMapping("/{id}")
    public String viewGoal(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Goal goal = goalService.getGoalById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        
        // Проверка доступа к цели
        if (!goal.getUser().getUsername().equals(auth.getName())) {
            return "error/403";
        }
        
        // Расчет процента выполнения, если есть целевое и текущее значение
        Integer progressPercentage = null;
        if (goal.getTargetValue() != null && goal.getCurrentValue() != null && goal.getTargetValue() > 0) {
            progressPercentage = Math.min(100, (int) (((double) goal.getCurrentValue() / goal.getTargetValue()) * 100));
        }
        
        model.addAttribute("goal", goal);
        model.addAttribute("progressPercentage", progressPercentage);
        
        return "goals/view";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("goalRequest", new GoalRequest());
        model.addAttribute("types", GOAL_TYPES);
        
        return "goals/create";
    }

    @PostMapping("/create")
    public String createGoal(
            @Valid @ModelAttribute("goalRequest") GoalRequest goalRequest,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("types", GOAL_TYPES);
            return "goals/create";
        }
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
        
        Goal goal = Goal.builder()
                .name(goalRequest.getName())
                .type(goalRequest.getType())
                .description(goalRequest.getDescription())
                .targetValue(goalRequest.getTargetValue())
                .currentValue(goalRequest.getCurrentValue())
                .unit(goalRequest.getUnit())
                .targetDate(goalRequest.getTargetDate())
                .completed(goalRequest.getCompleted() != null ? goalRequest.getCompleted() : false)
                .completedAt(goalRequest.getCompleted() != null && goalRequest.getCompleted() ? 
                        (goalRequest.getCompletedAt() != null ? goalRequest.getCompletedAt() : LocalDateTime.now()) : null)
                .user(user)
                .build();
        
        Goal createdGoal = goalService.createGoal(goal);
        
        redirectAttributes.addFlashAttribute("successMessage", "Цель успешно создана");
        
        return "redirect:/app/goals";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Goal goal = goalService.getGoalById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        
        // Проверка доступа к цели
        if (!goal.getUser().getUsername().equals(auth.getName())) {
            return "error/403";
        }
        
        GoalRequest goalRequest = GoalRequest.builder()
                .name(goal.getName())
                .type(goal.getType())
                .description(goal.getDescription())
                .targetValue(goal.getTargetValue())
                .currentValue(goal.getCurrentValue())
                .unit(goal.getUnit())
                .targetDate(goal.getTargetDate())
                .completed(goal.getCompleted())
                .completedAt(goal.getCompletedAt())
                .build();
        
        model.addAttribute("goalRequest", goalRequest);
        model.addAttribute("goalId", id);
        model.addAttribute("types", GOAL_TYPES);
        
        return "goals/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateGoal(
            @PathVariable Long id,
            @Valid @ModelAttribute("goalRequest") GoalRequest goalRequest,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("goalId", id);
            model.addAttribute("types", GOAL_TYPES);
            return "goals/edit";
        }
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Goal existingGoal = goalService.getGoalById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        
        // Проверка доступа к цели
        if (!existingGoal.getUser().getUsername().equals(auth.getName())) {
            return "error/403";
        }
        
        // Обновляем детали цели, не меняя пользователя
        existingGoal.setName(goalRequest.getName());
        existingGoal.setType(goalRequest.getType());
        existingGoal.setDescription(goalRequest.getDescription());
        existingGoal.setTargetValue(goalRequest.getTargetValue());
        existingGoal.setCurrentValue(goalRequest.getCurrentValue());
        existingGoal.setUnit(goalRequest.getUnit());
        existingGoal.setTargetDate(goalRequest.getTargetDate());
        
        // Обновляем статус завершения если он изменился
        if (Boolean.TRUE.equals(goalRequest.getCompleted()) && !Boolean.TRUE.equals(existingGoal.getCompleted())) {
            existingGoal.setCompleted(true);
            existingGoal.setCompletedAt(LocalDateTime.now());
        } else if (Boolean.FALSE.equals(goalRequest.getCompleted()) && Boolean.TRUE.equals(existingGoal.getCompleted())) {
            existingGoal.setCompleted(false);
            existingGoal.setCompletedAt(null);
        }
        
        goalService.updateGoal(id, existingGoal);
        
        redirectAttributes.addFlashAttribute("successMessage", "Цель успешно обновлена");
        
        return "redirect:/app/goals/" + id;
    }

    @PostMapping("/{id}/progress")
    public String updateProgress(
            @PathVariable Long id,
            @RequestParam Integer currentValue,
            RedirectAttributes redirectAttributes) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Goal existingGoal = goalService.getGoalById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        
        // Проверка доступа к цели
        if (!existingGoal.getUser().getUsername().equals(auth.getName())) {
            return "error/403";
        }
        
        // Обновляем прогресс
        goalService.updateProgress(id, currentValue);
        
        redirectAttributes.addFlashAttribute("successMessage", "Прогресс успешно обновлен");
        
        return "redirect:/app/goals/" + id;
    }

    @PostMapping("/{id}/complete")
    public String markAsCompleted(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Goal existingGoal = goalService.getGoalById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        
        // Проверка доступа к цели
        if (!existingGoal.getUser().getUsername().equals(auth.getName())) {
            return "error/403";
        }
        
        // Отмечаем цель как выполненную
        goalService.markAsCompleted(id);
        
        redirectAttributes.addFlashAttribute("successMessage", "Цель отмечена как выполненная");
        
        return "redirect:/app/goals/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteGoal(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Goal goal = goalService.getGoalById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        
        // Проверка доступа к цели
        if (!goal.getUser().getUsername().equals(auth.getName())) {
            return "error/403";
        }
        
        goalService.deleteGoal(id);
        
        redirectAttributes.addFlashAttribute("successMessage", "Цель успешно удалена");
        
        return "redirect:/app/goals";
    }
} 