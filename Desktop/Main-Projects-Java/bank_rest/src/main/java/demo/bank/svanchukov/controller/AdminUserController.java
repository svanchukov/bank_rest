package demo.bank.svanchukov.controller;

import demo.bank.svanchukov.dto.user.CreateNewUserDTO;
import demo.bank.svanchukov.dto.user.UserDTO;
import demo.bank.svanchukov.service.admin_service.AdminUserService;
import demo.bank.svanchukov.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin User Management", description = "Управление пользователями (создание, блокировка, активация, удаление)")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final UserService userService;

    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<UserDTO> users = adminUserService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.ok("Пользователей нет");
        }
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Создать нового пользователя", description = "Создаёт одного пользователя")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateNewUserDTO dto) {
            UserDTO createdUser = userService.createNewUser(dto);
            return ResponseEntity.ok(createdUser);
    }

    @Operation(summary = "Создать нескольких пользователей", description = "Создаёт сразу несколько пользователей")
    @PostMapping("/list")
    public List<UserDTO> createMany(@RequestBody List<@Valid CreateNewUserDTO> users) {
        return users.stream()
                .map(userService::createNewUser)
                .toList();
    }

    @Operation(summary = "Заблокировать пользователя", description = "Блокирует пользователя по его ID")
    @PostMapping("/{userId}/block")
    public ResponseEntity<Void> blockUser(@PathVariable("userId") Long userId) {
        try {
            adminUserService.blockedUser(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Активировать пользователя", description = "Активирует ранее заблокированного пользователя")
    @PostMapping("/{userId}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable("userId") Long userId) {
        try {
            adminUserService.activateUser(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по его ID")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
