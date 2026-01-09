package demo.bank.svanchukov.dto.user;

import demo.bank.svanchukov.entity.User;
import demo.bank.svanchukov.enum_Card_User.Role;
import demo.bank.svanchukov.enum_Card_User.UserStatus;
import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {
    
    private Long id;
    private String email;
    private String fio;
    private Role roles;
    private UserStatus status;


    public UserDTO(Long id, String email, String fio, UserStatus userStatus, Role role) {
    }
}
