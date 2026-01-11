package demo.bank.svanchukov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.bank.svanchukov.dto.card.CardDTO;
import demo.bank.svanchukov.dto.transfer.TransferRequestDTO;
import demo.bank.svanchukov.dto.transfer.TransferResponseDTO;
import demo.bank.svanchukov.enum_Card_User.CardStatus;
import demo.bank.svanchukov.service.CardService;
import demo.bank.svanchukov.service.TransferService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private TransferService transferService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Получение карт пользователя - Успешно")
    @WithMockUser
    void getUserCards_Success() throws Exception {
        Long userId = 1L;
        String expectedCardNumber = "**** **** **** 1111";

        CardDTO card = new CardDTO(10L, expectedCardNumber, BigDecimal.TEN, CardStatus.ACTIVE, userId, LocalDateTime.now());
        PageImpl<CardDTO> page = new PageImpl<>(List.of(card));

        when(cardService.getMyCards(eq(userId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/users/{userId}/cards", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cardNumber").value(expectedCardNumber));
    }

    @Test
    @DisplayName("Получение баланса - Успешно")
    @WithMockUser
    void getBalance_Success() throws Exception {
        Long userId = 1L;
        Long cardId = 10L;
        String expectedBalance = "1500.50";

        when(cardService.getBalance(userId, cardId)).thenReturn(new BigDecimal(expectedBalance));

        mockMvc.perform(get("/users/{userId}/cards/{cardId}/balance", userId, cardId))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedBalance));
    }

    @Test
    @DisplayName("Перевод денег - Успешно")
    @WithMockUser
    void transfer_Success() throws Exception {
        Long userId = 1L;
        TransferRequestDTO request = new TransferRequestDTO(10L, 20L, new BigDecimal("500.00"));
        TransferResponseDTO response = new TransferResponseDTO("Успех", 10L, 20L, new BigDecimal("500.00"));

        when(transferService.transfer(eq(userId), any(TransferRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/users/{userId}/transfer", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Успех"))
                .andExpect(jsonPath("$.amount").value(500.00));
    }

    @Test
    @DisplayName("Блокировка карты - Ошибка 403 (если не админ)")
    @WithMockUser(roles = "USER")
    void blockCard_ForbiddenForUser() throws Exception {
        mockMvc.perform(post("/users/1/cards/10/block")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Блокировка карты - Доступно для админа")
    @WithMockUser(roles = "ADMIN")
    void blockCard_OkForAdmin() throws Exception {
        mockMvc.perform(post("/users/1/cards/10/block")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}