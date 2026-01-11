package demo.bank.svanchukov.dto;

import lombok.Data;

@Data
public class AuthRequestDTO {

    private String email;
    private String password;
}
