package demo.bank.svanchukov.service;

import demo.bank.svanchukov.dto.transfer.TransferRequestDTO;
import demo.bank.svanchukov.dto.transfer.TransferResponseDTO;
import demo.bank.svanchukov.entity.Card;
import demo.bank.svanchukov.enum_Card_User.CardStatus;
import demo.bank.svanchukov.enum_Card_User.UserStatus;
import demo.bank.svanchukov.exception.*;
import demo.bank.svanchukov.exception.card.CardBlockedException;
import demo.bank.svanchukov.exception.card.CardExpiredException;
import demo.bank.svanchukov.exception.card.CardNotFoundException;
import demo.bank.svanchukov.exception.user.UserIsBlockedException;
import demo.bank.svanchukov.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final CardRepository cardRepository;

    @Transactional
    public TransferResponseDTO transfer(Long userId, TransferRequestDTO dto) {

        BigDecimal amount = dto.getAmount();

        // Проверка суммы перевода
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NoMoneyException("Сумма перевода должна быть больше 0");
        }

        // Получаем карты
        Card from = getCardForTransfer(dto.getFromCardId(), true, userId);
        Card to = getCardForTransfer(dto.getToCardId(), false, null);

        // Проверка срока действия карт
        checkCardExpiry(from);
        checkCardExpiry(to);

        // Проверка статусов карт
        if (from.getStatus() == CardStatus.BLOCKED || to.getStatus() == CardStatus.BLOCKED) {
            throw new CardBlockedException("Одна из карт заблокирована");
        }

        // Проверка баланса
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("На карте " + from.getCardNumber() +
                    " недостаточно средств, доступно: " + from.getBalance());
        }

        // Проверка статуса пользователя получателя
        if (to.getOwner().getUserStatus() == UserStatus.BLOCKED) {
            throw new UserIsBlockedException("Получатель заблокирован");
        }

        // Списание и зачисление
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        // Формируем ответ
        String message = String.format(
                "Перевод успешно выполнен: с карты %d на карту %d, сумма: %.2f",
                dto.getFromCardId(),
                dto.getToCardId(),
                amount
        );

        return new TransferResponseDTO(message, dto.getFromCardId(), dto.getToCardId(), amount);
    }

    // Проверка срока действия карты
    private void checkCardExpiry(Card card) {
        if (card.getExpiryDate().isBefore(LocalDateTime.now())) {
            card.setStatus(CardStatus.EXPIRED);
            throw new CardExpiredException("Срок действия карты истёк: " + card.getCardNumber());
        }
    }

    // Получение карты и проверка владельца
    private Card getCardForTransfer(Long cardId, boolean isOwner, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        if (isOwner && !card.getOwner().getId().equals(userId)) {
            throw new UnauthorizedCardTransferException("Попытка списания с чужой карты");
        }

        return card;
    }
}
