package com.example.fitness_tracker.service.impl;

import com.example.fitness_tracker.dto.CaloriesProgressDTO;
import com.example.fitness_tracker.dto.WorkoutStatsByTypeDTO;
import com.example.fitness_tracker.model.Workout;
import com.example.fitness_tracker.repository.WorkoutRepository;
import com.example.fitness_tracker.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {

    private final WorkoutRepository workoutRepository;
    
    @Autowired
    public StatsServiceImpl(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    @Override
    public List<WorkoutStatsByTypeDTO> getWorkoutStatsByType(Long userId) {
        // Получаем все тренировки пользователя
        List<Workout> userWorkouts = workoutRepository.findByUserId(userId);
        
        // Группируем тренировки по типу
        Map<String, List<Workout>> workoutsByType = userWorkouts.stream()
                .collect(Collectors.groupingBy(Workout::getType));
        
        // Формируем статистику по каждому типу
        List<WorkoutStatsByTypeDTO> statsList = new ArrayList<>();
        
        workoutsByType.forEach((type, workouts) -> {
            long count = workouts.size();
            int totalDuration = workouts.stream()
                    .mapToInt(Workout::getDuration)
                    .sum();
            int totalCalories = workouts.stream()
                    .mapToInt(Workout::getCaloriesBurned)
                    .sum();
            double avgDuration = count > 0 ? (double) totalDuration / count : 0;
            double avgCalories = count > 0 ? (double) totalCalories / count : 0;
            
            WorkoutStatsByTypeDTO stats = WorkoutStatsByTypeDTO.builder()
                    .type(type)
                    .count(count)
                    .totalDuration(totalDuration)
                    .totalCaloriesBurned(totalCalories)
                    .avgDuration(avgDuration)
                    .avgCaloriesBurned(avgCalories)
                    .build();
            
            statsList.add(stats);
        });
        
        return statsList;
    }

    @Override
    public List<CaloriesProgressDTO> getCaloriesProgress(Long userId, LocalDate startDate, LocalDate endDate, String period) {
        // Получаем тренировки пользователя за указанный период
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        List<Workout> workouts = workoutRepository.findByUserIdAndCreatedAtBetween(
                userId, startDateTime, endDateTime);
        
        List<CaloriesProgressDTO> progressList = new ArrayList<>();
        
        // Группируем в зависимости от выбранного периода
        switch (period.toLowerCase()) {
            case "day":
                // Группировка по дням
                Map<LocalDate, Integer> caloriesByDay = workouts.stream()
                        .collect(Collectors.groupingBy(
                                workout -> workout.getCreatedAt().toLocalDate(),
                                Collectors.summingInt(Workout::getCaloriesBurned)
                        ));
                
                // Заполняем все дни в диапазоне, даже если там не было тренировок
                LocalDate date = startDate;
                while (!date.isAfter(endDate)) {
                    int calories = caloriesByDay.getOrDefault(date, 0);
                    progressList.add(new CaloriesProgressDTO(date, calories, "day"));
                    date = date.plusDays(1);
                }
                break;
                
            case "week":
                // Группировка по неделям
                Map<String, Integer> caloriesByWeek = workouts.stream()
                        .collect(Collectors.groupingBy(
                                workout -> {
                                    LocalDate workoutDate = workout.getCreatedAt().toLocalDate();
                                    int weekOfYear = workoutDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                                    return workoutDate.getYear() + "-W" + String.format("%02d", weekOfYear);
                                },
                                Collectors.summingInt(Workout::getCaloriesBurned)
                        ));
                
                // Создаем список всех недель в диапазоне
                LocalDate weekDate = startDate;
                while (!weekDate.isAfter(endDate)) {
                    int weekOfYear = weekDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                    String weekKey = weekDate.getYear() + "-W" + String.format("%02d", weekOfYear);
                    int calories = caloriesByWeek.getOrDefault(weekKey, 0);
                    
                    // Начало недели как дата
                    LocalDate weekStartDate = weekDate.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
                    
                    progressList.add(new CaloriesProgressDTO(weekStartDate, calories, "week"));
                    weekDate = weekDate.plusWeeks(1);
                }
                break;
                
            case "month":
                // Группировка по месяцам
                Map<String, Integer> caloriesByMonth = workouts.stream()
                        .collect(Collectors.groupingBy(
                                workout -> {
                                    LocalDate workoutDate = workout.getCreatedAt().toLocalDate();
                                    return workoutDate.getYear() + "-" + String.format("%02d", workoutDate.getMonthValue());
                                },
                                Collectors.summingInt(Workout::getCaloriesBurned)
                        ));
                
                // Создаем список всех месяцев в диапазоне
                LocalDate monthDate = startDate.withDayOfMonth(1);
                while (!monthDate.isAfter(endDate)) {
                    String monthKey = monthDate.getYear() + "-" + String.format("%02d", monthDate.getMonthValue());
                    int calories = caloriesByMonth.getOrDefault(monthKey, 0);
                    
                    progressList.add(new CaloriesProgressDTO(monthDate, calories, "month"));
                    monthDate = monthDate.plusMonths(1);
                }
                break;
        }
        
        return progressList;
    }
} 