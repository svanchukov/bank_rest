package demo.bank.svanchukov.dto.card;

import demo.bank.svanchukov.enum_Card_User.CardStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CardDTO {

    private Long id;
    private String cardNumber;
    private Long ownerId;
    private LocalDateTime expiryDate;
    private CardStatus status;
    private BigDecimal balance;

    public CardDTO(Long id, String cardNumber, BigDecimal balance, CardStatus status, Long ownerId, LocalDateTime expiryDate) {
        this.id = id;
        this.cardNumber = maskNumber(cardNumber);
        this.balance = balance;
        this.status = status;
        this.ownerId = ownerId;
        this.expiryDate = expiryDate;
    }

    private String maskNumber(String number) {
        if (number == null || number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}
