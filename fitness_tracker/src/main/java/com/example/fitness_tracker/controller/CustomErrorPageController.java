package com.example.fitness_tracker.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class CustomErrorPageController implements ErrorController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomErrorPageController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response, Model model) {
        // Принудительно устанавливаем Content-Type
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        
        // Получаем информацию об ошибке
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String path = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String errorRequestUri = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        
        // Определяем код ошибки (по умолчанию 404 для неизвестных путей)
        int statusCode = statusObj != null ? Integer.parseInt(statusObj.toString()) : 404;
        
        // Проверяем аутентификацию
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = (auth != null && auth.isAuthenticated() && 
                                  !auth.getName().equals("anonymousUser"));
        
        // Проверяем, не происходит ли повторная обработка уже обрабатываемой ошибки
        if (errorRequestUri != null && errorRequestUri.startsWith("/error")) {
            logger.info("Предотвращено дублирование ошибки для пути: {}", path);
            return null; // Прерываем обработку, чтобы избежать зацикливания
        }
        
        // Логируем информацию для отладки
        logger.info("Ошибка: {} для пути: {}, пользователь аутентифицирован: {}", 
                   statusCode, path, isAuthenticated);
        
        // Важно: НЕ меняем код ошибки, а сохраняем исходный,
        // который был определен системой Spring Security

        // Устанавливаем соответствующий HTTP-статус
        response.setStatus(statusCode);
        
        // Добавляем данные в модель для отображения на странице
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("path", path);
        model.addAttribute("isAuthenticated", isAuthenticated);
        
        // Всегда используем один шаблон
        return "error/404";
    }
} 