package demo.bank.svanchukov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.bank.svanchukov.dto.user.CreateNewUserDTO;
import demo.bank.svanchukov.dto.user.UserDTO;
import demo.bank.svanchukov.enum_Card_User.Role;
import demo.bank.svanchukov.enum_Card_User.UserStatus;
import demo.bank.svanchukov.service.UserService;
import demo.bank.svanchukov.service.admin_service.AdminUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminUserService adminUserService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Получение всех пользователей - Список не пуст")
    void getAllUsers_ReturnsList() throws Exception {
        String validFio = "Иванов Иван Иванович";
        UserDTO userDTO = new UserDTO(1L, "test@mail.com", validFio, UserStatus.ACTIVE, Role.USER);

        when(adminUserService.getAllUsers()).thenReturn(List.of(userDTO));

        mockMvc.perform(get("/admin/users")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@mail.com"))
                .andExpect(jsonPath("$[0].fio").value(validFio));
    }

    @Test
    @DisplayName("Получение всех пользователей - Список пуст")
    void getAllUsers_EmptyList() throws Exception {
        when(adminUserService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/users")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string("Пользователей нет"));
    }

    @Test
    @DisplayName("Создание пользователя - Успешно")
    void createUser_Success() throws Exception {
        CreateNewUserDTO createDto = new CreateNewUserDTO();
        createDto.setEmail("new@mail.com");
        createDto.setFio("Сванчуков Сергей");
        createDto.setPassword("password123");

        UserDTO responseDto = new UserDTO(1L, "new@mail.com", "Сванчуков Сергей", UserStatus.ACTIVE, Role.USER);

        when(userService.createNewUser(any(CreateNewUserDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/admin/users")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@mail.com"))
                .andExpect(jsonPath("$.fio").value("Сванчуков Сергей"));
    }

    @Test
    @DisplayName("Заблокировать пользователя - Успешно")
    void blockUser_Success() throws Exception {
        mockMvc.perform(post("/admin/users/1/block")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Удалить пользователя - Успешно")
    void deleteUser_Success() throws Exception {
        mockMvc.perform(delete("/admin/users/1")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Ошибка доступа - Пользователь без роли ADMIN")
    void accessDenied_ForUserRole() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }
}