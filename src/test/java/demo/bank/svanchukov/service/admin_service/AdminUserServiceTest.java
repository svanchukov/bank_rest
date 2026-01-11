package demo.bank.svanchukov.service.admin_service;

import demo.bank.svanchukov.dto.user.UserDTO;
import demo.bank.svanchukov.entity.User;
import demo.bank.svanchukov.enum_Card_User.Role;
import demo.bank.svanchukov.enum_Card_User.UserStatus;
import demo.bank.svanchukov.exception.user.UserNotFoundException;
import demo.bank.svanchukov.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminUserService adminUserService;

    private User testUser;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("test@bank.com");
        testUser.setFio("Иван Иванов");
        testUser.setUserStatus(UserStatus.ACTIVE);
        testUser.setRole(Role.USER);
    }

    @Test
    @DisplayName("Успешная блокировка пользователя")
    void blockedUser_Success() {

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));


        adminUserService.blockedUser(userId);


        assertEquals(UserStatus.BLOCKED, testUser.getUserStatus(), "Статус должен измениться на BLOCKED");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Успешная активация пользователя")
    void activateUser_Success() {

        testUser.setUserStatus(UserStatus.BLOCKED);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));


        adminUserService.activateUser(userId);


        assertEquals(UserStatus.ACTIVE, testUser.getUserStatus(), "Статус должен измениться на ACTIVE");
    }

    @Test
    @DisplayName("Получение списка всех пользователей")
    void getAllUsers_Success() {

        when(userRepository.findAll()).thenReturn(List.of(testUser));


        List<UserDTO> result = adminUserService.getAllUsers();


        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getEmail(), result.get(0).getEmail());
        assertEquals(testUser.getRole(), result.get(0).getRoles());
    }

    @Test
    @DisplayName("Ошибка: пользователь не найден")
    void getUser_NotFound() {

        when(userRepository.findById(userId)).thenReturn(Optional.empty());


        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> adminUserService.blockedUser(userId));

        assertTrue(exception.getMessage().contains("Пользователь не найден"));
    }
}