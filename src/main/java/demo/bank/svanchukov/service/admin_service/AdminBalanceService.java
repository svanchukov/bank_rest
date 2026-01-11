package demo.bank.svanchukov.service.admin_service;

import demo.bank.svanchukov.dto.card.AdminTopUpCardDTO;
import demo.bank.svanchukov.entity.Card;
import demo.bank.svanchukov.enum_Card_User.CardStatus;
import demo.bank.svanchukov.exception.card.CardNotFoundException;
import demo.bank.svanchukov.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminBalanceService {

    private static final BigDecimal ADMIN_TOP_UP_AMOUNT = BigDecimal.valueOf(1000);

    private final CardRepository cardRepository;

    @Transactional
    public AdminTopUpCardDTO topUpToCard(Long cardId) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalArgumentException("Карта не активна, пополнение не возможно");
        }

        card.setBalance(card.getBalance().add(ADMIN_TOP_UP_AMOUNT));

        return new AdminTopUpCardDTO(
                cardId,
                ADMIN_TOP_UP_AMOUNT,
                card.getBalance(),
                "Пополнение карты с id: " + cardId + " успешно выполнено"
        );

    }
}
