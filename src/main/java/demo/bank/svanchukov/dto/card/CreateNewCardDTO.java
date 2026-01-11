package demo.bank.svanchukov.dto.card;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Data
public class CreateNewCardDTO {

    @NotNull(message = "Владелец карты обязателен")
    private Long ownerId;

}
