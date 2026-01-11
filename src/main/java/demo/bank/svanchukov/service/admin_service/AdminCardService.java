package demo.bank.svanchukov.service.admin_service;

import demo.bank.svanchukov.dto.card.CardDTO;
import demo.bank.svanchukov.dto.card.CreateNewCardDTO;
import demo.bank.svanchukov.entity.Card;
import demo.bank.svanchukov.entity.User;
import demo.bank.svanchukov.enum_Card_User.CardStatus;
import demo.bank.svanchukov.exception.card.CardNotFoundException;
import demo.bank.svanchukov.exception.user.UserNotFoundException;
import demo.bank.svanchukov.repository.CardRepository;
import demo.bank.svanchukov.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminCardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardDTO createCard(CreateNewCardDTO createNewCardDTO) {
        User user = userRepository.findById(createNewCardDTO.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден", createNewCardDTO.getOwnerId()));

        Card card = new Card();
        card.setCardNumber(generateCardNumber());
        card.setOwner(user);
        card.setExpiryDate(LocalDateTime.now().plusYears(3));
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);

        cardRepository.save(card);
        return toCardDTO(card);

    }

    @Transactional
    public void blockCard(Long cardId) {
        cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId))
                .setStatus(CardStatus.BLOCKED);
    }

    @Transactional
    public void activateCard(Long cardId) {
        cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId))
                .setStatus(CardStatus.ACTIVE);
    }

    @Transactional
    public void deleteCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                        .orElseThrow(() -> new CardNotFoundException(cardId));
        cardRepository.delete(card);
    }

    public Page<CardDTO> getAllCard(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(this::toCardDTO);
    }

    private String generateCardNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
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
