package demo.bank.svanchukov.controller;

import demo.bank.svanchukov.dto.auth.AuthRequest;
import demo.bank.svanchukov.dto.auth.AuthResponse;
import demo.bank.svanchukov.dto.user.CreateNewUserDTO;
import demo.bank.svanchukov.security.jwt.JwtService;
import demo.bank.svanchukov.service.UserService;
import demo.bank.svanchukov.service.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody CreateNewUserDTO createNewUserDTO) {
        log.info("Регистрация нового пользователя: {}", createNewUserDTO.getEmail());

        try {
            // Создаем пользователя
            var user = userService.createNewUser(createNewUserDTO);
            log.info("Пользователь создан: {}", user.getEmail());

            // Загружаем UserDetails
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
            log.info("Роли пользователя: {}", userDetails.getAuthorities());

            // Генерируем токен
            String token = jwtService.generateToken(userDetails);
            log.info("Токен сгенерирован успешно");

            return new AuthResponse(token);
        } catch (Exception e) {
            log.error("Ошибка при регистрации: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        log.info("Попытка входа пользователя: {}", request.getEmail());

        try {
            // Аутентифицируем пользователя
            log.info("Аутентификация через AuthenticationManager...");
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            log.info("Аутентификация успешна");

            // Загружаем UserDetails
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());
            log.info("Загружен пользователь: {}", userDetails.getUsername());
            log.info("Роли пользователя: {}", userDetails.getAuthorities());

            // Генерируем токен
            String token = jwtService.generateToken(userDetails);
            log.info("Токен сгенерирован");

            return new AuthResponse(token);
        } catch (Exception e) {
            log.error("Ошибка при входе: {}", e.getMessage(), e);
            throw e;
        }
    }
}