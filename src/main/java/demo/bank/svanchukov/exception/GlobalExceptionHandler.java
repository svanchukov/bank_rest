package demo.bank.svanchukov.exception;

import demo.bank.svanchukov.exception.card.CardAccessDeniedException;
import demo.bank.svanchukov.exception.card.CardBlockedException;
import demo.bank.svanchukov.exception.card.CardExpiredException;
import demo.bank.svanchukov.exception.user.EmailAlreadyExistsException;
import demo.bank.svanchukov.exception.user.UserIsBlockedException;
import demo.bank.svanchukov.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "VALIDATION_ERROR");

        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .toList();

        body.put("messages", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(jakarta.validation.ConstraintViolationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "LIST_VALIDATION_ERROR");

        // Вытаскиваем все ошибки из нарушения ограничений
        List<String> errors = ex.getConstraintViolations().stream()
                .map(violation -> {
                    String path = violation.getPropertyPath().toString();
                    return path + ": " + violation.getMessage();
                })
                .toList();

        body.put("messages", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Пользователь заблокирован
    @ExceptionHandler(UserIsBlockedException.class)
    public ResponseEntity<?> handleUserIsBlocked(UserIsBlockedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "USER_BLOCKED");
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "USER_NOT_FOUND");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Попытка списания с чужой карты
    @ExceptionHandler(UnauthorizedCardTransferException.class)
    public ResponseEntity<?> handleUnauthorizedCardAccess(UnauthorizedCardTransferException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "UNAUTHORIZED_CARD_ACCESS");
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    // Недостаточно средств или сумма <= 0
    @ExceptionHandler(NoMoneyException.class)
    public ResponseEntity<?> handleNoMoney(NoMoneyException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "NO_MONEY");
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<?> handleInsufficientFunds(InsufficientFundsException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "INSUFFICIENT_FUNDS");
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CardBlockedException.class)
    public ResponseEntity<?> handleCardBlocked(CardBlockedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "CARD_BLOCKED");
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CardExpiredException.class)
    public ResponseEntity<?> handleCardExpired(CardExpiredException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "CARD_EXPIRED");
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CardAccessDeniedException.class)
    public ResponseEntity<?> handleCardAccessDenied(CardAccessDeniedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "UNAUTHORIZED_CARD_ACCESS");
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value()); // 403

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<?> handleEmailExists(EmailAlreadyExistsException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "EMAIL_ALREADY_TAKEN");
        body.put("message", "Пользователь с email " + ex.getEmail() + " уже зарегистрирован");
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleBadJson(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "BAD_REQUEST");
        body.put("message", "Некорректный формат JSON или данных");
        // Можно добавить ex.getMostSpecificCause().getMessage() для отладки
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}
