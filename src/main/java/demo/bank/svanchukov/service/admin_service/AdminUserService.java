package demo.bank.svanchukov.service.admin_service;

import demo.bank.svanchukov.dto.user.UserDTO;
import demo.bank.svanchukov.entity.User;
import demo.bank.svanchukov.enum_Card_User.UserStatus;
import demo.bank.svanchukov.exception.user.UserNotFoundException;
import demo.bank.svanchukov.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    @Transactional
    public void blockedUser(Long userId) {
        User user = getUser(userId);
        user.setUserStatus(UserStatus.BLOCKED);
    }

    @Transactional
    public void activateUser(Long userId) {
        User user = getUser(userId);
        user.setUserStatus(UserStatus.ACTIVE);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toUserDTO)
                .toList();
    }


    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден", id));
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
