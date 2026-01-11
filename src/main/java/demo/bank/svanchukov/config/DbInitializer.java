package demo.bank.svanchukov.config;

import demo.bank.svanchukov.entity.User;
import demo.bank.svanchukov.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// Автоматическое создание админа
@Component
@RequiredArgsConstructor
@Slf4j
public class DbInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        String adminEmail = "admin@bank.com";

        userRepository.findByEmail(adminEmail).ifPresentOrElse(
                admin -> {
                    admin.setPassword(passwordEncoder.encode("admin123"));
                    userRepository.save(admin);
                    log.info("Пароль администратора {} был обновлен", adminEmail);
                },
                () -> {
                    User user = new User();
                    user.setEmail(adminEmail);
                    user.setPassword(passwordEncoder.encode("admin123"));
                    user.setFio("System Administrator");
                    user.setRole(demo.bank.svanchukov.enum_Card_User.Role.ADMIN);
                    user.setUserStatus(demo.bank.svanchukov.enum_Card_User.UserStatus.ACTIVE);

                    userRepository.save(user);
                    log.info("Администратор {} успешно создан!", adminEmail);
                }
        );
    }
}