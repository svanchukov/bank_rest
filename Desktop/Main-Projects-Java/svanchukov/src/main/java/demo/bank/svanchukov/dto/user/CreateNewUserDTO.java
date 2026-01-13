package demo.bank.svanchukov.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateNewUserDTO {

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неверный формат email")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 3, message = "Пароль должен быть минимум 3 символа")
    private String password;

    @NotBlank(message = "ФИО не может быть пустым")
    private String fio;
}
