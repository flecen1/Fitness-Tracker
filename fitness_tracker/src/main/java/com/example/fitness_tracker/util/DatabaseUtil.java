package com.example.fitness_tracker.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Утилитарный класс для выполнения операций с базой данных
 * Позволяет выполнять ручные миграции без потери данных
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseUtil {
    
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * Безопасно добавляет колонку в таблицу, если она не существует
     * @param tableName имя таблицы
     * @param columnName имя колонки
     * @param columnDefinition определение колонки (тип и другие атрибуты)
     */
    public void addColumnIfNotExists(String tableName, String columnName, String columnDefinition) {
        try {
            // Проверяем, существует ли колонка
            String checkColumnSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_NAME = ? AND COLUMN_NAME = ? AND TABLE_SCHEMA = DATABASE()";
            
            Integer count = jdbcTemplate.queryForObject(checkColumnSql, Integer.class, tableName, columnName);
            
            if (count != null && count == 0) {
                // Колонка не существует, добавляем её
                String addColumnSql = String.format("ALTER TABLE %s ADD COLUMN %s %s", 
                        tableName, columnName, columnDefinition);
                
                jdbcTemplate.execute(addColumnSql);
                log.info("Колонка {} добавлена в таблицу {}", columnName, tableName);
            } else {
                log.info("Колонка {} уже существует в таблице {}", columnName, tableName);
            }
        } catch (Exception e) {
            log.error("Ошибка при добавлении колонки {} в таблицу {}: {}", 
                    columnName, tableName, e.getMessage());
        }
    }
    
    /**
     * Выполняет произвольный SQL-запрос
     * @param sql SQL-запрос для выполнения
     */
    public void executeSQL(String sql) {
        try {
            jdbcTemplate.execute(sql);
            log.info("SQL-запрос успешно выполнен: {}", sql);
        } catch (Exception e) {
            log.error("Ошибка при выполнении SQL-запроса {}: {}", sql, e.getMessage());
        }
    }
} 