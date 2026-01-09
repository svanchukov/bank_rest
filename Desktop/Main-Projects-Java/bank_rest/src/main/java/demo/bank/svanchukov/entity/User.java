package demo.bank.svanchukov.entity;

import demo.bank.svanchukov.enum_Card_User.Role;
import demo.bank.svanchukov.enum_Card_User.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "table_users")
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false ,unique = true)
    private String email;

    @Column(name = "fio", nullable = false, unique = true)
    private String fio;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "userStatus")
    private UserStatus userStatus;
}
