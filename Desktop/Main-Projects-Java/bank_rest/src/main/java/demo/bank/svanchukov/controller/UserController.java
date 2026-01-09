package demo.bank.svanchukov.controller;

import demo.bank.svanchukov.dto.TransferRequestDTO;
import demo.bank.svanchukov.dto.card.CardDTO;
import demo.bank.svanchukov.service.CardService;
import demo.bank.svanchukov.service.TransferService;
import demo.bank.svanchukov.service.UserService;
import demo.bank.svanchukov.service.security.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/users/my")
@RequiredArgsConstructor
public class UserController {

    private final CardService cardService;
    private final AuthService authService;
    private final UserService userService;
    private final TransferService transferService;

    @GetMapping("/cards")
    public ResponseEntity<Page<CardDTO>> getMyCards(Pageable pageable) {
        Long userId = authService.getCurrentUserId();
        Page<CardDTO> cards = cardService.getMyCards(userId, pageable);
        if (cards.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/cards/{cardId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable("cardId") Long cardId) {
        Long userId = authService.getCurrentUserId();
        try {
            BigDecimal balance = cardService.getBalance(userId, cardId);
            return ResponseEntity.ok(balance);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping("/cards/{cardId}/block")
    public ResponseEntity<Void> requestBlock(@PathVariable("cardId") Long cardId) {
        Long userId = authService.getCurrentUserId();
        try {
            cardService.requestBlock(userId, cardId);
            return ResponseEntity.ok().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequestDTO transferRequestDTO) {
        Long userId = authService.getCurrentUserId();
        try {
            transferService.transfer(userId, transferRequestDTO);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
