package demo.bank.svanchukov.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserIsBlockedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserIsBlockedException(String message) {
        super(message);
    }

    public UserIsBlockedException() {
        this("Пользователь заблокирован");
    }
}