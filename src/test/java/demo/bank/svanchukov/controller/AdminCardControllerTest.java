package demo.bank.svanchukov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.bank.svanchukov.dto.card.AdminTopUpCardDTO;
import demo.bank.svanchukov.dto.card.CardDTO;
import demo.bank.svanchukov.dto.card.CreateNewCardDTO;
import demo.bank.svanchukov.enum_Card_User.CardStatus;
import demo.bank.svanchukov.service.admin_service.AdminBalanceService;
import demo.bank.svanchukov.service.admin_service.AdminCardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminCardService adminCardService;

    @MockBean
    private AdminBalanceService adminBalanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Создание карты - Успешно (Admin)")
    void createCard_Success() throws Exception {
        CreateNewCardDTO createDto = new CreateNewCardDTO();
        createDto.setOwnerId(1L);

        CardDTO cardDto = new CardDTO(1L, "**** **** **** 1234", BigDecimal.ZERO,
                CardStatus.ACTIVE, 1L, LocalDateTime.now());

        when(adminCardService.createCard(any(CreateNewCardDTO.class))).thenReturn(cardDto);

        mockMvc.perform(post("/admin/cards/create")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Пополнение карты - Успешно")
    void adminTopUp_Success() throws Exception {
        // Arrange
        Long cardId = 1L;
        BigDecimal topUpAmount = BigDecimal.valueOf(1000);
        BigDecimal finalBalance = BigDecimal.valueOf(1500);

        AdminTopUpCardDTO responseDto = new AdminTopUpCardDTO(
                cardId,
                topUpAmount,
                finalBalance,
                "Успешно"
        );

        when(adminBalanceService.topUpToCard(cardId)).thenReturn(responseDto);

        mockMvc.perform(post("/admin/cards/{cardId}/top-up", cardId)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cardId").value(cardId))
                .andExpect(jsonPath("$.amount").value(1000))
                .andExpect(jsonPath("$.balanceAfter").value(1500))
                .andExpect(jsonPath("$.message").value("Успешно"));
    }

    @Test
    @DisplayName("Блокировка карты - No Content")
    void blockCard_Success() throws Exception {
        mockMvc.perform(post("/admin/cards/1/block")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Удаление карты - No Content")
    void deleteCard_Success() throws Exception {
        mockMvc.perform(delete("/admin/cards/1")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Ошибка доступа - Обычный пользователь не может зайти в админку")
    void adminAccess_Forbidden() throws Exception {
        mockMvc.perform(get("/admin/cards")
                        .with(user("user").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}