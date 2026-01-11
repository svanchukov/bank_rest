package demo.bank.svanchukov.controller;

import demo.bank.svanchukov.dto.card.CardDTO;
import demo.bank.svanchukov.service.CardService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "Работа с картами: просмотр, баланс, блокировка")
public class CardController {

    private final CardService cardService;

    @Operation(summary = "Получить все свои карты", description = "Возвращает постраничный список карт пользователя")
    @GetMapping
    public ResponseEntity<Page<CardDTO>> getMyCards(@RequestParam Long userId, @Parameter(hidden = true) Pageable pageable) {
        Page<CardDTO> cards = cardService.getMyCards(userId, pageable);
        if (cards.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cards);
    }

    @Operation(summary = "Получить баланс карты", description = "Возвращает баланс конкретной карты пользователя")
    @GetMapping("/{cardId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@RequestParam Long userId, @PathVariable Long cardId) {
        try {
            BigDecimal balance = cardService.getBalance(userId, cardId);
            return ResponseEntity.ok(balance);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @Operation(summary = "Заблокировать карту", description = "Позволяет пользователю запросить блокировку своей карты")
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
