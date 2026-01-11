package demo.bank.svanchukov.service;

import demo.bank.svanchukov.dto.user.CreateNewUserDTO;
import demo.bank.svanchukov.dto.user.UserDTO;
import demo.bank.svanchukov.entity.User;
import demo.bank.svanchukov.enum_Card_User.Role;
import demo.bank.svanchukov.enum_Card_User.UserStatus;
import demo.bank.svanchukov.exception.user.EmailAlreadyExistsException;
import demo.bank.svanchukov.exception.user.UserNotFoundException;
import demo.bank.svanchukov.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private final Long userId = 1L;
    private final String email = "test@bank.com";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail(email);
        testUser.setFio("Иван Иванов");
        testUser.setUserStatus(UserStatus.ACTIVE);
        testUser.setRole(Role.USER);
        testUser.setPassword("encoded_password");
    }

    @Test
    @DisplayName("Успешное создание пользователя")
    void createNewUser_Success() {
        CreateNewUserDTO dto = new CreateNewUserDTO();
        dto.setEmail(email);
        dto.setFio("Иван Иванов");
        dto.setPassword("raw_password");

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode("raw_password")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(userId);
            return savedUser;
        });

        UserDTO result = userService.createNewUser(dto);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(UserStatus.ACTIVE, result.getStatus());
        verify(passwordEncoder).encode("raw_password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Ошибка при создании: email уже существует")
    void createNewUser_EmailAlreadyExists() {
        CreateNewUserDTO dto = new CreateNewUserDTO();
        dto.setEmail(email);

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createNewUser(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Успешное обновление данных пользователя")
    void updateUser_Success() {

        CreateNewUserDTO updateDto = new CreateNewUserDTO();
        updateDto.setEmail("new@bank.com");
        updateDto.setFio("Петр Петров");
        updateDto.setPassword("");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("new@bank.com")).thenReturn(false);


        UserDTO result = userService.updateUser(userId, updateDto);


        assertEquals("new@bank.com", result.getEmail());
        assertEquals("Петр Петров", result.getFio());
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Успешное удаление пользователя")
    void deleteUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        userService.deleteUser(userId);

        verify(userRepository).delete(testUser);
    }

    @Test
    @DisplayName("Ошибка: пользователь для удаления не найден")
    void deleteUser_NotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(email, result.get(0).getEmail());
    }
}