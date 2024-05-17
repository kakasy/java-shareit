package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {
    @Mock
    private RequestClient requestClient;
    @InjectMocks
    private RequestController itemRequestController;
    private MockMvc mockMvc;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();


    private static final String USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void startUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();
    }

    @SneakyThrows
    @Test
    void createItemRequest_whenEmptyItemRequest_thenReturnError() {
        ItemRequestDto invalidRequest = ItemRequestDto.builder()
                .description("")
                .build();

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyNoInteractions(requestClient);
    }

    @SneakyThrows
    @Test
    void createItemRequest_whenBigInvalidItemRequest_thenReturnError() {
        ItemRequestDto invalidRequest = ItemRequestDto.builder()
                .description("Реализовать юнит-тесты для всего кода, содержащего логику. " +
                        "Выберите те классы, которые содержат в себе нетривиальные методы, условия и ветвления. " +
                        "В основном это будут классы сервисов. Напишите юнит-тесты на все такие методы, используя моки при необходимости.\n" +
                        "Реализовать интеграционные тесты, проверяющие взаимодействие с базой данных. Как вы помните, " +
                        "интеграционные тесты представляют собой более высокий уровень тестирования: их обычно требуется " +
                        "меньше, но покрытие каждого — больше. Мы предлагаем вам создать по одному интеграционному тесту" +
                        "для каждого крупного метода в ваших сервисах. Например, для метода getUserItems в классе ItemServiceImpl.")
                .build();

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyNoInteractions(requestClient);
    }
}