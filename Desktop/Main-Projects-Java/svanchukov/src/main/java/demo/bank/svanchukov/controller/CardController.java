package demo.bank.svanchukov.controller;

import demo.bank.svanchukov.dto.card.CardDTO;
import demo.bank.svanchukov.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<Page<CardDTO>> getMyCards(@RequestParam Long userId, Pageable pageable) {
        Page<CardDTO> cards = cardService.getMyCards(userId, pageable);
        if (cards.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{cardId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@RequestParam Long userId, @PathVariable Long cardId) {
        try {
            BigDecimal balance = cardService.getBalance(userId, cardId);
            return ResponseEntity.ok(balance);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping("/{cardId}/block")
    public ResponseEntity<Void> requestBlock(@RequestParam Long userId, @PathVariable Long cardId) {
        try {
            cardService.requestBlock(userId, cardId);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        }
    }

}
























