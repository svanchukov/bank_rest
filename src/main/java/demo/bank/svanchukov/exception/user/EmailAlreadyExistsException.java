package demo.bank.svanchukov.exception.user;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String email;

    public EmailAlreadyExistsException(String email) {
        super(String.format("Пользователь с email [%s] уже существует", email));
        this.email = email;
    }
}