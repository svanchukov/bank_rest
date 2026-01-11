package demo.bank.svanchukov.service;

import demo.bank.svanchukov.dto.user.CreateNewUserDTO;
import demo.bank.svanchukov.dto.user.UserDTO;
import demo.bank.svanchukov.entity.User;
import demo.bank.svanchukov.enum_Card_User.Role;
import demo.bank.svanchukov.enum_Card_User.UserStatus;
import demo.bank.svanchukov.exception.user.EmailAlreadyExistsException;
import demo.bank.svanchukov.exception.user.UserNotFoundException;
import demo.bank.svanchukov.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        log.info("Запрос на получение всех пользователей");

        List<UserDTO> users = userRepository.findAll()
                .stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());

        log.info("Найдено пользователей: {}", users.size());
        return users;
    }

    public UserDTO getUserById(Long id) {
        log.info("Запрос на получение пользователя с id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id: {} не найден", id);
                    return new UserNotFoundException("Пользователь не найден", id);
                });

        log.debug("Пользователь найден: email: {}", user.getEmail());
        return toUserDTO(user);
    }

    public UserDTO createNewUser(CreateNewUserDTO dto) {
        log.info("Создание нового пользователя с email: {}", dto.getEmail());

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        User user = new User();
        user.setFio(dto.getFio());
        user.setEmail(dto.getEmail());
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(encodedPassword);
        user.setUserStatus(UserStatus.ACTIVE);
        user.setRole(Role.USER);

        userRepository.save(user);

        log.info("Пользователь успешно создан, id: {}", user.getId());
        return toUserDTO(user);
    }

    @Transactional
    public UserDTO updateUser(Long id, CreateNewUserDTO dto) {
        log.info("Обновление пользователя с id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Попытка обновления несуществующего пользователя id: {}", id);
                    return new UserNotFoundException("Пользователь не найден", id);
                });

        if (!user.getEmail().equals(dto.getEmail())
                && userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        user.setFio(dto.getFio());
        user.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            log.debug("Обновление пароля для пользователя id: {}", id);
            user.setPassword(dto.getPassword());
        }

        userRepository.save(user);

        log.info("Пользователь с id: {} успешно обновлён", id);
        return toUserDTO(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Попытка удалить несуществующего пользователя с ID {}", id);
                    return new UserNotFoundException("Пользователь не найден", id);
                });

        userRepository.delete(user);

        log.info("Пользователь с ID {} успешно удалён", id);
    }

    private UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFio(),
                user.getUserStatus(),
                user.getRole()
        );
    }
}
