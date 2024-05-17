package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemClient itemClient;
    @InjectMocks
    private ItemController itemController;
    private MockMvc mockMvc;
    private ItemShortDto itemShortDto;
    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();


    private static final String USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void startUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        itemShortDto = ItemShortDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();
    }

    @SneakyThrows
    @Test
    void createItem_whenInvalidItemName_thenReturnBadRequest() {
        
        itemShortDto = ItemShortDto.builder()
                .name("")
                .build();

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemShortDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyNoInteractions(itemClient);


    }

    @SneakyThrows
    @Test
    void createItem_whenInvalidItemDescription_thenReturnBadRequest() {
        
        itemShortDto = ItemShortDto.builder()
                .description("")
                .build();

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemShortDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyNoInteractions(itemClient);
    }

    @SneakyThrows
    @Test
    void createItem_whenInvalidItemAvailable_thenReturnBadRequest() {
        
        itemShortDto = ItemShortDto.builder()
                .available(null)
                .build();

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemShortDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyNoInteractions(itemClient);
    }

    @SneakyThrows
    @Test
    void updateItem_whenInvalidItemDescription_thenReturnBadRequest() {

        Long id = 1L;
        itemShortDto = ItemShortDto.builder()
                .description("Реализовать юнит-тесты для всего кода, содержащего логику. \" +\n" +
                        "Выберите те классы, которые содержат в себе нетривиальные методы, условия и ветвления. \" +\n" +
                        "В основном это будут классы сервисов. Напишите юнит-тесты на все такие методы, используя моки при необходимости.\\n\" +\n" +
                        "Реализовать интеграционные тесты, проверяющие взаимодействие с базой данных. Как вы помните, \" +\n" +
                        "интеграционные тесты представляют собой более высокий уровень тестирования: их обычно требуется \" +\n" +
                        "меньше, но покрытие каждого — больше. Мы предлагаем вам создать по одному интеграционному тесту\" +\n" +
                        "для каждого крупного метода в ваших сервисах. Например, для метода getUserItems в классе ItemServiceImpl.")
                .build();
        

        mockMvc.perform(patch("/items/{itemId}", id)
                        .content(objectMapper.writeValueAsString(itemShortDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyNoInteractions(itemClient);
    }

    @SneakyThrows
    @Test
    void createComment_whenInvalidCommentSize_thenReturnBadRequest() {
        
        Long id = 1L;
        
        CommentShortDto commentRequest = CommentShortDto.builder()
                .text("")
                .build();
        

        mockMvc.perform(post("/items/{itemId}/comment", id)
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyNoInteractions(itemClient);
    }

}
