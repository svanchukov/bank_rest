package demo.bank.svanchukov.service;

import demo.bank.svanchukov.dto.card.CardDTO;
import demo.bank.svanchukov.entity.Card;
import demo.bank.svanchukov.entity.User;
import demo.bank.svanchukov.enum_Card_User.CardStatus;
import demo.bank.svanchukov.exception.card.CardAccessDeniedException;
import demo.bank.svanchukov.exception.card.CardNotFoundException;
import demo.bank.svanchukov.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    private User owner;
    private Card testCard;
    private final Long userId = 1L;
    private final Long cardId = 10L;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(userId);

        testCard = new Card();
        testCard.setId(cardId);
        testCard.setOwner(owner);
        testCard.setBalance(BigDecimal.valueOf(100.00));
        testCard.setStatus(CardStatus.ACTIVE);
        testCard.setCardNumber("1111222233334444");
    }

    @Test
    @DisplayName("Успешное получение своих карт")
    void getMyCards_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> cardPage = new PageImpl<>(List.of(testCard));

        when(cardRepository.findByOwnerId(userId, pageable)).thenReturn(cardPage);

        Page<CardDTO> result = cardService.getMyCards(userId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(cardId, result.getContent().get(0).getId());
    }

    @Test
    @DisplayName("Успешное получение баланса своей карты")
    void getBalance_Success() throws AccessDeniedException {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));

        BigDecimal balance = cardService.getBalance(userId, cardId);

        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(balance));
    }

    @Test
    @DisplayName("Блокировка своей карты")
    void requestBlock_Success() throws AccessDeniedException {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));

        cardService.requestBlock(userId, cardId);

        assertEquals(CardStatus.BLOCKED, testCard.getStatus());
    }

    @Test
    @DisplayName("Ошибка доступа: попытка получить баланс чужой карты")
    void getBalance_AccessDenied() {
        Long strangerId = 999L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));

        assertThrows(CardAccessDeniedException.class,
                () -> cardService.getBalance(strangerId, cardId));
    }

    @Test
    @DisplayName("Ошибка: карта не найдена")
    void getUserCard_NotFound() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class,
                () -> cardService.getBalance(userId, cardId));
    }
}