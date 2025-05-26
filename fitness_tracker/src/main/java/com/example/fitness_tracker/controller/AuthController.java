package com.example.fitness_tracker.controller;

import com.example.fitness_tracker.dto.*;
import com.example.fitness_tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping("/users/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody RegisterRequest request) {
        try {
            UserDto user = userService.register(request);
            return ResponseEntity.ok(ApiResponse.success("Пользователь успешно зарегистрирован", user));
        } catch (Exception e) {
            log.error("Ошибка при регистрации пользователя", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/verify-with-code")
    public ResponseEntity<ApiResponse<UserDto>> verifyWithCode(@RequestBody VerificationRequest request) {
        try {
            UserDto user = userService.verifyWithCode(request);
            return ResponseEntity.ok(ApiResponse.success("Аккаунт успешно подтвержден", user));
        } catch (Exception e) {
            log.error("Ошибка при верификации кода", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestBody ResendVerificationRequest request) {
        try {
            userService.resendVerificationCode(request.getUsername(), request.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Код верификации отправлен на ваш email"));
        } catch (Exception e) {
            log.error("Ошибка при повторной отправке кода верификации", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
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
} 