package demo.bank.svanchukov.dto.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransferResponseDTO {

    private String message;

    private Long fromCardId;

    private Long toCardId;

    private BigDecimal amount;
}