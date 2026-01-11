package demo.bank.svanchukov.controller;

import demo.bank.svanchukov.dto.card.AdminTopUpCardDTO;
import demo.bank.svanchukov.dto.card.CardDTO;
import demo.bank.svanchukov.dto.card.CreateNewCardDTO;
import demo.bank.svanchukov.service.admin_service.AdminBalanceService;
import demo.bank.svanchukov.service.admin_service.AdminCardService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/cards")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Card Management", description = "Управление картами (создание, блокировка, пополнение и удаление)")
public class AdminCardController {

    private final AdminCardService adminCardService;
    private final AdminBalanceService adminBalanceService;

    @Operation(summary = "Создать новую карту", description = "Создаёт карту для указанного пользователя")
    @PostMapping("/create")
    public ResponseEntity<CardDTO> createCard(@Valid @RequestBody CreateNewCardDTO createNewCardDTO) {
        CardDTO createdCard = adminCardService.createCard(createNewCardDTO);
        return ResponseEntity.ok(createdCard);
    }

    @Operation(summary = "Заблокировать карту", description = "Блокирует карту по её ID")
    @PostMapping("/{cardId}/block")
    public ResponseEntity<Void> blockCard(@PathVariable Long cardId) {
        adminCardService.blockCard(cardId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Активировать карту", description = "Активирует ранее заблокированную карту")
    @PostMapping("/{cardId}/activate")
    public ResponseEntity<Void> activateCard(@PathVariable Long cardId) {
        adminCardService.activateCard(cardId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Пополнить карту", description = "Пополняет баланс карты")
    @PostMapping("/{cardId}/top-up")
    public ResponseEntity<AdminTopUpCardDTO> adminTopUp(@PathVariable("cardId") Long cardId) {
        return ResponseEntity.ok(adminBalanceService.topUpToCard(cardId));
    }

    @Operation(summary = "Удалить карту", description = "Удаляет карту по её ID")
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        adminCardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить все карты", description = "Возвращает страницу карт с пагинацией")
    @GetMapping
    public ResponseEntity<Page<CardDTO>> getAllCards(@Parameter(hidden = true) Pageable pageable) {
        Page<CardDTO> cards = adminCardService.getAllCard(pageable);
        if (cards.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cards);
    }
}
