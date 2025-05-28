package com.example.fitness_tracker.service;

import com.example.fitness_tracker.dto.RegisterRequest;
import com.example.fitness_tracker.dto.UserDto;
import com.example.fitness_tracker.dto.VerificationRequest;
import com.example.fitness_tracker.model.User;
import com.example.fitness_tracker.model.VerificationCode;
import com.example.fitness_tracker.repository.UserRepository;
import com.example.fitness_tracker.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final VerificationCodeRepository codeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final Random random = new Random();
    
    @Autowired(required = false)
    private AuthenticationManager authenticationManager;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public UserDto register(RegisterRequest request) {
        // Проверяем существование пользователя
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        // Проверяем email, если предоставлен
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        // Создаем нового пользователя
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .verified(false)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        log.info("Пользователь зарегистрирован: {}", savedUser.getUsername());

        // Если предоставлен email, отправляем код верификации
        if (request.getEmail() != null) {
            String verificationCode = generateVerificationCode();
            sendVerificationCode(savedUser, verificationCode);
        }

        return UserDto.fromUser(savedUser);
    }

    @Transactional
    public UserDto verifyWithCode(VerificationRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Ищем последний код верификации
        VerificationCode verificationCode = codeRepository.findFirstByUserOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new RuntimeException("Код подтверждения не найден или истек"));

        // Проверяем код
        if (!request.getCode().equals(verificationCode.getCode())) {
            throw new RuntimeException("Неверный код подтверждения");
        }

        // Проверяем срок действия
        if (verificationCode.isExpired()) {
            throw new RuntimeException("Код подтверждения истек");
        }

        // Верифицируем пользователя
        user.setVerified(true);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Удаляем использованный код
        codeRepository.delete(verificationCode);

        // Программно аутентифицируем пользователя
        try {
            // Создаем токен аутентификации (без пароля, так как пользователь уже прошёл верификацию)
            Authentication authToken = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
            
            // Устанавливаем аутентификацию в контекст безопасности
            SecurityContextHolder.getContext().setAuthentication(authToken);
            
            log.info("Пользователь верифицирован и аутентифицирован: {}", user.getUsername());
        } catch (Exception e) {
            log.error("Ошибка при аутентификации после верификации: {}", e.getMessage());
        }

        return UserDto.fromUser(user);
    }

    @Transactional
    public void resendVerificationCode(String username, String email) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Проверяем, не верифицирован ли пользователь
        if (user.isVerified()) {
            throw new RuntimeException("Пользователь уже верифицирован");
        }

        // Проверяем совпадение email
        if (!email.equals(user.getEmail())) {
            throw new RuntimeException("Указанный email не совпадает с email пользователя");
        }

        // Удаляем старые коды
        codeRepository.deleteAllByUser(user);

        // Генерируем и отправляем новый код
        String newCode = generateVerificationCode();
        sendVerificationCode(user, newCode);

        log.info("Код верификации повторно отправлен пользователю: {}", username);
    }

    private String generateVerificationCode() {
        // Генерируем 6-значный код
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private void sendVerificationCode(User user, String code) {
        // Создаем запись о коде в БД
        VerificationCode verificationCode = VerificationCode.builder()
                .user(user)
                .code(code)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30)) // Код действителен 30 минут
                .build();

        codeRepository.save(verificationCode);

        // Отправляем код на email
        emailService.sendVerificationEmail(user.getEmail(), code);
    }

    public boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isUserVerified(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.isPresent() && userOpt.get().isVerified();
    }

    @Transactional
    public void verifyUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        user.setVerified(true);
        userRepository.save(user);
        log.info("Пользователь верифицирован администратором: {}", username);
    }
} 