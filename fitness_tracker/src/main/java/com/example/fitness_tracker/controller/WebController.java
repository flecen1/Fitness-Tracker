package com.example.fitness_tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/verification")
    public String verification() {
        return "verification";
    }

    // Страницы для авторизованных пользователей
    @GetMapping("/app/dashboard")
    public String dashboard() {
        return "app/dashboard";
    }

    @GetMapping("/app/goals")
    public String goals() {
        return "app/goals";
    }

    @GetMapping("/app/stats")
    public String stats() {
        return "app/stats";
    }

    @GetMapping("/app/profile")
    public String profile() {
        return "app/profile";
    }
} 