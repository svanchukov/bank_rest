package demo.bank.svanchukov.controller;

import demo.bank.svanchukov.dto.AuthRequestDTO;
import demo.bank.svanchukov.security.jwt.JwtService;
import demo.bank.svanchukov.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Авторизация и получение токена")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    @Operation(summary = "Вход в систему", description = "Возвращает JWT токен при успешном совпадении логина и пароля")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequestDTO  request) {
        UserDetails user = userDetailsService.loadUserByUsername(request.getEmail());

        // Логирование для отладки
        System.out.println("Пароль из запроса: " + request.getPassword());
        System.out.println("Хеш из базы: " + user.getPassword());

        boolean isMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
        System.out.println("Результат сравнения: " + isMatch);

        if (isMatch) {
            return ResponseEntity.ok(jwtService.generateToken(user));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
    }
}