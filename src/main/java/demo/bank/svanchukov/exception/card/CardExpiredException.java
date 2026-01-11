package demo.bank.svanchukov.exception.card;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class CardExpiredException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CardExpiredException(String message) {
        super(message);
    }

    public CardExpiredException() {
        super("Срок действия карты истёк");
    }
}