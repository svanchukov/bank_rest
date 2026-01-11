package demo.bank.svanchukov.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedCardTransferException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedCardTransferException(String message) {
        super(message);
    }

    public UnauthorizedCardTransferException() {
        this("Попытка списания с чужой карты");
    }
}
