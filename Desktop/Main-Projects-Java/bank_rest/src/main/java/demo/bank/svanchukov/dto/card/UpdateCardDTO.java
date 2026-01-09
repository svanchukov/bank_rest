package demo.bank.svanchukov.dto.card;

import demo.bank.svanchukov.enum_Card_User.CardStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCardDTO {

    @NotNull(message = "ID карты обязателен")
    private Long id;

    @NotNull(message = "Статус карты обязателен")
    private CardStatus cardStatus;
}
