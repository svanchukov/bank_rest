package demo.bank.svanchukov.dto.user;

import demo.bank.svanchukov.entity.User;
import demo.bank.svanchukov.enum_Card_User.Role;
import lombok.Data;

import java.util.Set;

@Data
public class AdminUserDTO {

    private Long id;
    private String email;
    private String fio;
    private Set<Role> roles;
    private User status;
}
