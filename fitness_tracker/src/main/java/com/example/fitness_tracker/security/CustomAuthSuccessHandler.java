package com.example.fitness_tracker.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public CustomAuthSuccessHandler() {
        super();
        // Устанавливаем URL по умолчанию для перенаправления после успешной аутентификации
        setDefaultTargetUrl("/app/dashboard");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) 
            throws IOException, ServletException {
        
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_LAST_USERNAME", authentication.getName());
        
        log.info("Пользователь '{}' успешно аутентифицирован, перенаправление на дашборд", 
                authentication.getName());
        
        // Сохраняем имя пользователя в сессии
        session.setAttribute("username", authentication.getName());
        
        // Вызываем родительский метод для стандартной обработки
        super.onAuthenticationSuccess(request, response, authentication);
    }
} 