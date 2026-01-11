package demo.bank.svanchukov.service;

import demo.bank.svanchukov.dto.transfer.TransferRequestDTO;
import demo.bank.svanchukov.dto.transfer.TransferResponseDTO;
import demo.bank.svanchukov.entity.Card;
import demo.bank.svanchukov.entity.User;
import demo.bank.svanchukov.enum_Card_User.CardStatus;
import demo.bank.svanchukov.enum_Card_User.UserStatus;
import demo.bank.svanchukov.exception.*;
import demo.bank.svanchukov.exception.card.CardBlockedException;
import demo.bank.svanchukov.exception.card.CardExpiredException;
import demo.bank.svanchukov.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private TransferService transferService;

    private User sender;
    private User receiver;
    private Card fromCard;
    private Card toCard;
    private final Long senderId = 1L;
    private final Long fromCardId = 10L;
    private final Long toCardId = 20L;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(senderId);
        sender.setUserStatus(UserStatus.ACTIVE);

        receiver = new User();
        receiver.setId(2L);
        receiver.setUserStatus(UserStatus.ACTIVE);

        fromCard = new Card();
        fromCard.setId(fromCardId);
        fromCard.setOwner(sender);
        fromCard.setBalance(BigDecimal.valueOf(1000.00));
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setExpiryDate(LocalDateTime.now().plusYears(1));

        toCard = new Card();
        toCard.setId(toCardId);
        toCard.setOwner(receiver);
        toCard.setBalance(BigDecimal.valueOf(500.00));
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setExpiryDate(LocalDateTime.now().plusYears(1));
    }

    @Test
    @DisplayName("Успешный перевод между картами")
    void transfer_Success() {
        TransferRequestDTO dto = new TransferRequestDTO(fromCardId, toCardId, BigDecimal.valueOf(300.00));

        when(cardRepository.findById(fromCardId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCardId)).thenReturn(Optional.of(toCard));

        TransferResponseDTO response = transferService.transfer(senderId, dto);

        assertAll(
                () -> assertEquals(0, BigDecimal.valueOf(700.00).compareTo(fromCard.getBalance())),
                () -> assertEquals(0, BigDecimal.valueOf(800.00).compareTo(toCard.getBalance())),
                () -> assertEquals(fromCardId, response.getFromCardId()),
                () -> assertTrue(response.getMessage().contains("успешно выполнен"))
        );
    }

    @Test
    @DisplayName("Ошибка: недостаточно средств")
    void transfer_InsufficientFunds() {
        TransferRequestDTO dto = new TransferRequestDTO(fromCardId, toCardId, BigDecimal.valueOf(2000.00));

        when(cardRepository.findById(fromCardId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCardId)).thenReturn(Optional.of(toCard));

        assertThrows(InsufficientFundsException.class, () -> transferService.transfer(senderId, dto));
    }

    @Test
    @DisplayName("Ошибка: попытка списания с чужой карты")
    void transfer_UnauthorizedAccess() {
        TransferRequestDTO dto = new TransferRequestDTO(fromCardId, toCardId, BigDecimal.valueOf(100.00));
        Long wrongUserId = 999L;

        when(cardRepository.findById(fromCardId)).thenReturn(Optional.of(fromCard));

        assertThrows(UnauthorizedCardTransferException.class, () -> transferService.transfer(wrongUserId, dto));
    }

    @Test
    @DisplayName("Ошибка: карта заблокирована")
    void transfer_CardBlocked() {
        fromCard.setStatus(CardStatus.BLOCKED);
        TransferRequestDTO dto = new TransferRequestDTO(fromCardId, toCardId, BigDecimal.valueOf(100.00));

        when(cardRepository.findById(fromCardId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCardId)).thenReturn(Optional.of(toCard));

        assertThrows(CardBlockedException.class, () -> transferService.transfer(senderId, dto));
    }

    @Test
    @DisplayName("Ошибка: срок действия карты истек")
    void transfer_CardExpired() {
        fromCard.setExpiryDate(LocalDateTime.now().minusDays(1));
        TransferRequestDTO dto = new TransferRequestDTO(fromCardId, toCardId, BigDecimal.valueOf(100.00));

        when(cardRepository.findById(fromCardId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCardId)).thenReturn(Optional.of(toCard));

        assertThrows(CardExpiredException.class, () -> transferService.transfer(senderId, dto));
        assertEquals(CardStatus.EXPIRED, fromCard.getStatus());
    }
}