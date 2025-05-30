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

@Controller
public class CustomErrorPageController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response, Model model) {
        // Принудительно устанавливаем Content-Type
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        
        // Получаем статус ошибки
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = status != null ? Integer.valueOf(status.toString()) : 500;
        
        // Проверяем, аутентифицирован ли пользователь
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() 
                && !authentication.getName().equals("anonymousUser");
        
        // Для аутентифицированных пользователей перенаправляем на dashboard
        if (isAuthenticated) {
            return "redirect:/app/dashboard";
        }
        
        // Для ошибок 404 - возвращаем страницу Not Found
        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            model.addAttribute("statusCode", 404);
            model.addAttribute("errorMessage", "Страница не найдена");
            return "error/404";
        }
        
        // Исключаем коды перенаправлений
        if (statusCode >= 300 && statusCode < 400) {
            return null; // Позволяем продолжить обработку перенаправлений
        }
        
        // Для других ошибок показываем общую страницу ошибки
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", "Произошла ошибка");
        return "error/error";
    }
} 