package demo.bank.svanchukov.service.admin_service;

import demo.bank.svanchukov.dto.card.AdminTopUpCardDTO;
import demo.bank.svanchukov.entity.Card;
import demo.bank.svanchukov.enum_Card_User.CardStatus;
import demo.bank.svanchukov.exception.card.CardNotFoundException;
import demo.bank.svanchukov.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminBalanceServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private AdminBalanceService adminBalanceService;

    private Card testCard;
    private final Long cardId = 1L;

    @BeforeEach
    void setUp() {
        testCard = new Card();
        testCard.setId(cardId);
        testCard.setBalance(BigDecimal.valueOf(500));
        testCard.setStatus(CardStatus.ACTIVE);
    }

    @Test
    @DisplayName("Успешное пополнение активной карты")
    void topUpToCard_Success() {

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));

        AdminTopUpCardDTO result = adminBalanceService.topUpToCard(cardId);

        assertEquals(0, BigDecimal.valueOf(1500).compareTo(testCard.getBalance()), "Баланс должен быть 1500");

        assertTrue(result.getMessage().contains("успешно выполнено"));
    }

    @Test
    @DisplayName("Ошибка, если карта не найдена")
    void topUpToCard_NotFound() {

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> adminBalanceService.topUpToCard(cardId));
        verify(cardRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ошибка, если карта заблокирована")
    void topUpToCard_CardNotActive() {

        testCard.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminBalanceService.topUpToCard(cardId));

        assertEquals("Карта не активна, пополнение не возможно", exception.getMessage());
        assertEquals(BigDecimal.valueOf(500), testCard.getBalance(), "Баланс не должен измениться");
    }
}