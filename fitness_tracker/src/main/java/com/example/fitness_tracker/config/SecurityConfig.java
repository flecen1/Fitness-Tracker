package com.example.fitness_tracker.config;

import com.example.fitness_tracker.dto.ApiResponse;
import com.example.fitness_tracker.security.CustomAuthSuccessHandler;
import com.example.fitness_tracker.security.CustomAuthenticationProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationProvider authenticationProvider;
    private final CustomAuthSuccessHandler authSuccessHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public SecurityConfig(CustomAuthenticationProvider authenticationProvider,
                          CustomAuthSuccessHandler authSuccessHandler) {
        this.authenticationProvider = authenticationProvider;
        this.authSuccessHandler = authSuccessHandler;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        return authenticationManagerBuilder.build();
    }
    
    // Определяем матчер для REST API запросов
    private RequestMatcher apiRequestMatcher() {
        return new AntPathRequestMatcher("/api/**");
    }
    
    // Обработчик ошибок доступа для API
    private AccessDeniedHandler apiAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), 
                ApiResponse.error("Доступ запрещен. Недостаточно прав для выполнения операции."));
        };
    }
    
    // Обработчик ошибок аутентификации для API
    private AuthenticationFailureHandler apiAuthFailureHandler() {
        return (HttpServletRequest request, HttpServletResponse response, AuthenticationException e) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), 
                ApiResponse.error("Ошибка аутентификации: " + e.getMessage()));
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Создаем отдельный entry point для API запросов
        var loginEntryPoint = new LoginUrlAuthenticationEntryPoint("/login");
        AuthenticationEntryPoint apiAuthenticationEntryPoint = (request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(),
                ApiResponse.error("Требуется аутентификация для доступа к ресурсу"));
        };

        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/register").permitAll()
                .requestMatchers("/api/login").permitAll()
                .requestMatchers("/api/verify-with-code").permitAll()
                .requestMatchers("/api/resend-verification").permitAll()
                .requestMatchers("/api/users/*/exists").permitAll()
                .requestMatchers("/api/users/email/*/exists").permitAll()
                .requestMatchers("/api/workouts/**").authenticated()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/error/**").permitAll()
                .requestMatchers("/").permitAll()
                .requestMatchers("/register").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/verification").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                .requestMatchers("/app/**").authenticated()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedHandler(apiAccessDeniedHandler())
                .defaultAuthenticationEntryPointFor(
                    apiAuthenticationEntryPoint,
                    apiRequestMatcher() 
                )
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/app/dashboard", true)
                .successHandler(authSuccessHandler)
                .usernameParameter("username")
                .passwordParameter("password")
                .failureHandler((request, response, e) -> {
                    // Проверяем, если это API запрос
                    if (apiRequestMatcher().matches(request)) {
                        apiAuthFailureHandler().onAuthenticationFailure(request, response, e);
                    } else {
                        // Для обычных веб-запросов редирект на страницу логина с ошибкой
                        response.sendRedirect("/login?error");
                    }
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredUrl("/login?expired")
            )
            .headers(headers -> headers.contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors 'self'")));
        
        return http.build();
    }
} 