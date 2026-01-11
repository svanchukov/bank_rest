package demo.bank.svanchukov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.bank.svanchukov.dto.AuthRequestDTO;
import demo.bank.svanchukov.security.jwt.JwtService;
import demo.bank.svanchukov.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 1. Используем WebMvcTest вместо SpringBootTest, чтобы не запускать базу и DbInitializer
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 2. Мокаем все зависимости, которые есть в конструкторе AuthController
    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("Вход - Успех: выдача токена при верном пароле")
    void login_Success() throws Exception {
        // Arrange
        String email = "test@bank.com";
        String password = "correct_password";
        String hashedPassword = "hashed_password_in_db";
        String mockToken = "mocked.jwt.token";

        AuthRequestDTO request = new AuthRequestDTO();
        request.setEmail(email);
        request.setPassword(password);

        UserDetails userDetails = new User(email, hashedPassword, Collections.emptyList());

        // Настраиваем поведение моков
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(mockToken);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .with(csrf()) // Добавляем CSRF, иначе Spring Security выдаст 403
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(mockToken));
    }

    @Test
    @DisplayName("Вход - Ошибка 401: неверный пароль")
    void login_WrongPassword() throws Exception {
        // Arrange
        AuthRequestDTO request = new AuthRequestDTO();
        request.setEmail("test@bank.com");
        request.setPassword("wrong_password");

        UserDetails userDetails = new User("test@bank.com", "hashed_password", Collections.emptyList());

        when(userDetailsService.loadUserByUsername("test@bank.com")).thenReturn(userDetails);
        when(passwordEncoder.matches("wrong_password", "hashed_password")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Неверный логин или пароль"));
    }
}