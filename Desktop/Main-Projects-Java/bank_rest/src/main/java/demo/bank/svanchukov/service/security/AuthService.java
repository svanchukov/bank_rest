package demo.bank.svanchukov.service.security;

import demo.bank.svanchukov.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails users = (CustomUserDetails) authentication.getPrincipal();

        return users.getId();
    }
}
