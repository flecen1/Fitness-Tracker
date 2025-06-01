package com.example.fitness_tracker.controller;

import com.example.fitness_tracker.dto.*;
import com.example.fitness_tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping(value = "/users/register", 
                produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE, "*/*"})
    public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody RegisterRequest request) {
        try {
            UserDto user = userService.register(request);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ApiResponse.success("Пользователь успешно зарегистрирован", user));
        } catch (Exception e) {
            log.error("Ошибка при регистрации пользователя", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<?> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
        log.warn("Обрабатываем HttpMediaTypeNotAcceptableException: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Ошибка формата данных");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<?> handleHttpMessageNotWritableException(HttpMessageNotWritableException ex) {
        log.warn("Обрабатываем HttpMessageNotWritableException: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Ошибка при обработке запроса");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/verify-with-code", 
               produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE, "*/*"})
    public ResponseEntity<Object> verifyWithCode(@RequestBody VerificationRequest request) {
        try {
            log.debug("Получен запрос на верификацию кода для пользователя: {}, код: {}", 
                     request.getUsername(), request.getCode());
            UserDto user = userService.verifyWithCode(request);
            log.info("Верификация успешна для пользователя: {}", request.getUsername());
            
            // Создаем ответ в формате JSON
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Аккаунт успешно подтвержден");
            response.put("data", user);
            
            // Устанавливаем правильные заголовки CORS и Content-Type
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Access-Control-Allow-Origin", "*");
            
            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Ошибка при верификации кода для пользователя {}: {}", 
                     request.getUsername(), e.getMessage());
                     
            // Создаем ответ об ошибке в формате JSON
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Access-Control-Allow-Origin", "*");
            
            return new ResponseEntity<>(errorResponse, headers, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/resend-verification",
               produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE, "*/*"})
    public ResponseEntity<Object> resendVerification(@RequestBody ResendVerificationRequest request) {
        try {
            userService.resendVerificationCode(request.getUsername(), request.getEmail());
            
            // Создаем ответ в формате JSON
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Код верификации отправлен на ваш email");
            
            // Устанавливаем правильные заголовки CORS и Content-Type
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Access-Control-Allow-Origin", "*");
            
            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Ошибка при повторной отправке кода верификации", e);
            
            // Создаем ответ об ошибке в формате JSON
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Access-Control-Allow-Origin", "*");
            
            return new ResponseEntity<>(errorResponse, headers, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/users/{username}/verified")
    public ResponseEntity<ApiResponse<Boolean>> checkUserVerified(@PathVariable String username) {
        try {
            boolean verified = userService.isUserVerified(username);
            return ResponseEntity.ok(ApiResponse.success("Проверка верификации", verified));
        } catch (Exception e) {
            log.error("Ошибка при проверке верификации", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/verify/{username}")
    public ResponseEntity<ApiResponse<Void>> verifyUser(@PathVariable String username) {
        try {
            userService.verifyUser(username);
            return ResponseEntity.ok(ApiResponse.success("Пользователь успешно верифицирован"));
        } catch (Exception e) {
            log.error("Ошибка при верификации пользователя", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/users/{username}/exists")
    public ResponseEntity<ApiResponse<Boolean>> checkUsernameExists(@PathVariable String username) {
        try {
            boolean exists = userService.checkUsernameExists(username);
            return ResponseEntity.ok(ApiResponse.success("Проверка существования имени пользователя", exists));
        } catch (Exception e) {
            log.error("Ошибка при проверке существования имени пользователя", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/users/email/{email}/exists")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(@PathVariable String email) {
        try {
            boolean exists = userService.checkEmailExists(email);
            return ResponseEntity.ok(ApiResponse.success("Проверка существования email", exists));
        } catch (Exception e) {
            log.error("Ошибка при проверке существования email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Обработка OPTIONS-запросов для поддержки CORS
     */
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptions() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept");
        headers.add("Access-Control-Max-Age", "3600");
        
        return ResponseEntity.ok().headers(headers).build();
    }
} 