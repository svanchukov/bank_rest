package demo.bank.svanchukov.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AdminTopUpCardDTO {

    private Long cardId;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String message;

}
