package demo.bank.svanchukov.service;

import demo.bank.svanchukov.dto.card.CardDTO;
import demo.bank.svanchukov.entity.Card;
import demo.bank.svanchukov.enum_Card_User.CardStatus;
import demo.bank.svanchukov.exception.card.CardAccessDeniedException;
import demo.bank.svanchukov.exception.card.CardNotFoundException;
import demo.bank.svanchukov.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    public Page<CardDTO> getMyCards(Long userId, Pageable pageable) {
        return cardRepository.findByOwnerId(userId, pageable)
                .map(this::toCardDTO);
    }

    public BigDecimal getBalance(Long userId, Long cardId) throws AccessDeniedException {
        Card card = getUserCard(userId, cardId);
        return card.getBalance();
    }

    @Transactional
    public void requestBlock(Long userId, Long cardId) throws AccessDeniedException {
        Card card = getUserCard(userId, cardId);
        card.setStatus(CardStatus.BLOCKED);
    }

    private Card getUserCard(Long userId, Long cardId) throws AccessDeniedException {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        if (!card.getOwner().getId().equals(userId)) {
            throw new CardAccessDeniedException("Попытка доступа к чужой карте");
        }

        return card;
    }

    private CardDTO toCardDTO(Card card) {
        return new CardDTO(
                card.getId(),
                card.getCardNumber(),
                card.getBalance(),
                card.getStatus(),
                card.getOwner().getId(),
                card.getExpiryDate()
        );
    }


}
