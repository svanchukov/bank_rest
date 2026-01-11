package demo.bank.svanchukov.controller;

import demo.bank.svanchukov.dto.card.CardDTO;
import demo.bank.svanchukov.enum_Card_User.CardStatus;
import demo.bank.svanchukov.exception.card.CardAccessDeniedException;
import demo.bank.svanchukov.service.CardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @Test
    @DisplayName("Получение списка своих карт - Успешно")
    @WithMockUser
    void getMyCards_Success() throws Exception {
        Long userId = 1L;
        CardDTO card = new CardDTO(10L, "4444********1111", BigDecimal.valueOf(500),
                CardStatus.ACTIVE, userId, LocalDateTime.now());

        PageImpl<CardDTO> cardPage = new PageImpl<>(List.of(card));

        when(cardService.getMyCards(eq(userId), any(Pageable.class))).thenReturn(cardPage);

        mockMvc.perform(get("/cards")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].balance").value(500));
    }

    @Test
    @DisplayName("Получение баланса - Ошибка доступа (403)")
    @WithMockUser
    void getBalance_AccessDenied() throws Exception {
        Long userId = 1L;
        Long cardId = 99L;

        when(cardService.getBalance(userId, cardId)).thenThrow(new CardAccessDeniedException("Чужая карта"));

        mockMvc.perform(get("/cards/{cardId}/balance", cardId)
                        .param("userId", userId.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Блокировка карты - Успешно")
    @WithMockUser
    void requestBlock_Success() throws Exception {
        Long userId = 1L;
        Long cardId = 10L;

        mockMvc.perform(post("/cards/{cardId}/block", cardId)
                        .param("userId", userId.toString())
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Получение карт - Возврат 204 если список пуст")
    @WithMockUser
    void getMyCards_Empty() throws Exception {
        Long userId = 1L;
        when(cardService.getMyCards(eq(userId), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/cards")
                        .param("userId", userId.toString()))
                .andExpect(status().isNoContent());
    }
}