package demo.bank.svanchukov.service;

import demo.bank.svanchukov.dto.TransferRequestDTO;
import demo.bank.svanchukov.entity.Card;
import demo.bank.svanchukov.enum_Card_User.CardStatus;
import demo.bank.svanchukov.exception.CardBlockedException;
import demo.bank.svanchukov.exception.CardNotFoundException;
import demo.bank.svanchukov.exception.InsufficientFundsException;
import demo.bank.svanchukov.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final CardRepository cardRepository;

    @Transactional
    public void transfer(Long userId, TransferRequestDTO dto) {

        BigDecimal amount = dto.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть больше 0");
        }

        Card from = getCardForTransfer(dto.getFromCardId(), true, userId);
        Card to = getCardForTransfer(dto.getToCardId(), false, null);

        if (from.getStatus() == CardStatus.BLOCKED || to.getStatus() == CardStatus.BLOCKED) {
            throw new CardBlockedException("Одна из карт заблокирована");
        }

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("На карте " + from.getCardNumber() + " недостаточно средств " +
                    "средств на карте: " + from.getBalance());
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
    }


    private Card getCardForTransfer(Long cardId, boolean isOwner, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        if (isOwner && !card.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Попытка списания с чужой карты");
        }

        return card;
    }
}
