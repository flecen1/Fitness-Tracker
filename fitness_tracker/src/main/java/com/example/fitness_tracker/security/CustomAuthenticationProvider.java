package com.example.fitness_tracker.security;

import com.example.fitness_tracker.model.User;
import com.example.fitness_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        log.debug("Попытка аутентификации для пользователя: {}", username);

        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BadCredentialsException("Неверное имя пользователя или пароль"));

            if (!passwordEncoder.matches(password, user.getPassword())) {
                log.debug("Неверный пароль для пользователя: {}", username);
                throw new BadCredentialsException("Неверное имя пользователя или пароль");
            }

            if (!user.isVerified()) {
                log.debug("Пользователь не верифицирован: {}", username);
                throw new BadCredentialsException("Аккаунт не подтвержден. Пожалуйста, проверьте почту для верификации.");
            }

            // Обновляем время последнего входа
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            log.info("Пользователь успешно аутентифицирован через форму: {}", username);
            return new UsernamePasswordAuthenticationToken(username, null, authorities);
        } catch (Exception e) {
            log.error("Ошибка при аутентификации пользователя {}: {}", username, e.getMessage());
            throw new BadCredentialsException("Ошибка аутентификации: " + e.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
} 