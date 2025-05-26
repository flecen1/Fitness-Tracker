package com.example.fitness_tracker.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNotFound(HttpServletResponse response) {
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setStatus(HttpStatus.NOT_FOUND.value());

        ModelAndView mav = new ModelAndView();
        mav.addObject("statusCode", 404);
        mav.addObject("errorMessage", "Страница не найдена");
        mav.setViewName("error/404");
        return mav;
    }
} 