package demo.bank.svanchukov.exception.card;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class CardAccessDeniedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CardAccessDeniedException(String message) {
        super(message);
    }
}
