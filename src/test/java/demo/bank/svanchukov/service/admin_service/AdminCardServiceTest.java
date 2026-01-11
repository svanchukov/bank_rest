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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminCardService adminCardService;

    private User testUser;
    private Card testCard;
    private final Long userId = 1L;
    private final Long cardId = 10L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);

        testCard = new Card();
        testCard.setId(cardId);
        testCard.setOwner(testUser);
        testCard.setCardNumber("1234123412341234");
        testCard.setStatus(CardStatus.ACTIVE);
        testCard.setBalance(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Успешное создание карты с маскированием номера")
    void createCard_Success() {
        CreateNewCardDTO createDto = new CreateNewCardDTO();
        createDto.setOwnerId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        CardDTO result = adminCardService.createCard(createDto);


        assertNotNull(result);
        assertEquals(userId, result.getOwnerId());

        assertEquals(19, result.getCardNumber().length(), "Номер должен быть замаскирован (19 символов)");

        assertTrue(result.getCardNumber().startsWith("****"), "Номер карты должен содержать маску");

        verify(cardRepository).save(any(Card.class));
    }

    @Test
    @DisplayName("Блокировка карты")
    void blockCard_Success() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));

        adminCardService.blockCard(cardId);

        assertEquals(CardStatus.BLOCKED, testCard.getStatus());
    }

    @Test
    @DisplayName("Активация карты")
    void activateCard_Success() {
        testCard.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));

        adminCardService.activateCard(cardId);

        assertEquals(CardStatus.ACTIVE, testCard.getStatus());
    }

    @Test
    @DisplayName("Удаление карты")
    void deleteCard_Success() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));

        adminCardService.deleteCard(cardId);

        verify(cardRepository, times(1)).delete(testCard);
    }

    @Test
    @DisplayName("Получение всех карт с пагинацией")
    void getAllCard_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> cardPage = new PageImpl<>(List.of(testCard));

        when(cardRepository.findAll(pageable)).thenReturn(cardPage);

        Page<CardDTO> result = adminCardService.getAllCard(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(cardId, result.getContent().get(0).getId());
    }

    @Test
    @DisplayName("Ошибка при создании карты: пользователь не найден")
    void createCard_UserNotFound() {
        CreateNewCardDTO createDto = new CreateNewCardDTO();
        createDto.setOwnerId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> adminCardService.createCard(createDto));
    }
}