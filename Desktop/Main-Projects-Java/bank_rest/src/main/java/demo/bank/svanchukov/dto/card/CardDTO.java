package demo.bank.svanchukov.dto.card;

import demo.bank.svanchukov.enum_Card_User.CardStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
public class CardDTO {

    private Long id;
    private String cardNumber;
    private Long ownerId;
    private YearMonth expiryDate;
    private CardStatus status;
    private BigDecimal balance;

    public CardDTO(Long id, String cardNumber, BigDecimal balance, CardStatus status, Long owner, YearMonth expiryDate) {
    }
}
