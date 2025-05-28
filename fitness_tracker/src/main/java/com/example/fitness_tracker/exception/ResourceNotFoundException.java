package com.example.fitness_tracker.exception;

import jakarta.persistence.EntityNotFoundException;

/**
 * Исключение, которое выбрасывается, когда запрашиваемый ресурс не найден.
 * Расширяет EntityNotFoundException для автоматической обработки в GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends EntityNotFoundException {
    
    private String resourceName;
    private String fieldName;
    private Object fieldValue;
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s не найден с %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public String getResourceName() {
        return resourceName;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Object getFieldValue() {
        return fieldValue;
    }
} 