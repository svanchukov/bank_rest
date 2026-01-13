package demo.bank.svanchukov.controller;

import demo.bank.svanchukov.dto.card.CardDTO;
import demo.bank.svanchukov.dto.card.CreateNewCardDTO;
import demo.bank.svanchukov.service.AdminCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/cards")
public class AdminCardController {

    private final AdminCardService adminCardService;

    @PostMapping("/create")
    public ResponseEntity<CardDTO> createCard(@Valid @RequestBody CreateNewCardDTO createNewCardDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        CardDTO createdCard = adminCardService.createCard(createNewCardDTO);
        return ResponseEntity.ok(createdCard);
    }

    @PostMapping("/{cardId}/block")
    public ResponseEntity<Void> blockCard(@PathVariable Long cardId) {
        adminCardService.blockCard(cardId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cardId}/activate")
    public ResponseEntity<Void> activateCard(@PathVariable Long cardId) {
        adminCardService.activateCard(cardId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        adminCardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<CardDTO>> getAllCards(Pageable pageable) {
        Page<CardDTO> cards = adminCardService.getAllCard(pageable);
        if (cards.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cards);
    }
}
