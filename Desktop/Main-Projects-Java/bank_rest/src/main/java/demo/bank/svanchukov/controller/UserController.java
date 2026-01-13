package demo.bank.svanchukov.controller;

import demo.bank.svanchukov.dto.transfer.TransferRequestDTO;
import demo.bank.svanchukov.dto.card.CardDTO;
import demo.bank.svanchukov.dto.transfer.TransferResponseDTO;
import demo.bank.svanchukov.service.CardService;
import demo.bank.svanchukov.service.TransferService;
import demo.bank.svanchukov.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
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
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Operations", description = "Действия пользователя: карты, баланс, блокировка, переводы")
public class UserController {

    private final CardService cardService;
    private final UserService userService;
    private final TransferService transferService;

    @Operation(summary = "Получить карты пользователя", description = "Возвращает постраничный список карт пользователя")
    @GetMapping("/{userId}/cards")
    public ResponseEntity<Page<CardDTO>> getUserCards(@PathVariable("userId") Long userId, @Parameter(hidden = true) Pageable pageable) {
        Page<CardDTO> cards = cardService.getMyCards(userId, pageable);
        if (cards.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cards);
    }

    @Operation(summary = "Получить баланс карты", description = "Возвращает баланс конкретной карты пользователя")
    @GetMapping("/{userId}/cards/{cardId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable("userId") Long userId, @PathVariable("cardId") Long cardId ) throws AccessDeniedException {
        return ResponseEntity.ok(cardService.getBalance(userId, cardId));
    }

    @Operation(summary = "Заблокировать карту", description = "Позволяет пользователю заблокировать свою карту")
    @PostMapping("/{userId}/cards/{cardId}/block")
    public ResponseEntity<Void> blockCard(@PathVariable("userId") Long userId, @PathVariable("cardId") Long cardId) {
        try {
            cardService.requestBlock(userId, cardId);
            return ResponseEntity.ok().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @Operation(summary = "Перевод денег", description = "Позволяет пользователю перевести деньги с одной карты на другую")
    @PostMapping("/{userId}/transfer")
    public ResponseEntity<TransferResponseDTO> transfer(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody TransferRequestDTO dto) {
        return ResponseEntity.ok(transferService.transfer(userId, dto));
    }
}
