package demo.bank.svanchukov.security;

import demo.bank.svanchukov.enum_Card_User.Role;
import demo.bank.svanchukov.enum_Card_User.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Set<Role> roles;
    private final UserStatus userStatus;

    public CustomUserDetails(
            Long id,
            String email,
            String password,
            Role roles,
            UserStatus userStatus
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.roles = Set.of(roles);
        this.userStatus = userStatus;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(
                        "ROLE_" + role.name()
                ))
                .toList();
    }

    @Override public String getPassword() {
        return password;
    }

    @Override public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return userStatus == UserStatus.ACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return userStatus == UserStatus.ACTIVE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userStatus == UserStatus.ACTIVE;
    }

    @Override
    public boolean isEnabled() {
        return userStatus == UserStatus.ACTIVE;
    }
}


