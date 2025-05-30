package com.example.fitness_tracker.service;

import com.example.fitness_tracker.dto.CaloriesProgressDTO;
import com.example.fitness_tracker.dto.WorkoutStatsByTypeDTO;

import java.time.LocalDate;
import java.util.List;

public interface StatsService {
    
    /**
     * Получение статистики тренировок по типам
     * @param userId ID пользователя
     * @return список статистики по каждому типу тренировок
     */
    List<WorkoutStatsByTypeDTO> getWorkoutStatsByType(Long userId);
    
    /**
     * Получение статистики прогресса сожженных калорий за указанный период
     * @param userId ID пользователя
     * @param startDate начальная дата периода
     * @param endDate конечная дата периода
     * @param period тип группировки (day, week, month)
     * @return список прогресса по калориям за период
     */
    List<CaloriesProgressDTO> getCaloriesProgress(Long userId, LocalDate startDate, LocalDate endDate, String period);
} 