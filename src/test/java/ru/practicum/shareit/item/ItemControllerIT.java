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
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerIT {

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    ItemController itemController;

    @Mock
    private ItemServiceImpl itemService;

    private ItemShortDto itemShortDto;

    private ItemShortDto itemRequest;

    private ItemResponseDto itemResponseDto;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void startUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        itemShortDto = ItemShortDto.builder()
                .id(1L)
                .name("Item")
                .description("item description")
                .available(true)
                .requestId(1L)
                .build();

        itemRequest = ItemShortDto.builder()
                .name("Item")
                .description("item description")
                .available(true)
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Item")
                .description("item description")
                .available(true)
                .requestId(1L)
                .lastBooking(null)
                .nextBooking(null)
                .comments(List.of())
                .build();
    }

    @SneakyThrows
    @Test
    void createItem_whenValidItem_thenReturnItem() {

        when(itemService.createItemDto(any(ItemShortDto.class), anyLong())).thenReturn(itemShortDto);

        String result = mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemShortDto), result);
    }

    @SneakyThrows
    @Test
    void createItem_whenItemNameIsEmpty_thenReturnBadRequest() {

        ItemShortDto itemShortDto1 = itemRequest;

        itemShortDto1.setName("");

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verifyNoInteractions(itemService);
    }

    @SneakyThrows
    @Test
    void createItem_whenItemDescriptionIsEmpty_thenReturnBadRequest()  {

        ItemShortDto itemShortDto1 = itemRequest;
        itemShortDto1.setDescription("");

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verifyNoInteractions(itemService);
    }

    @SneakyThrows
    @Test
    void createItem_whenItemAvailableIsNull_thenReturnBadRequest() {

        ItemShortDto itemShortDto1 = itemRequest;
        itemShortDto1.setAvailable(null);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verifyNoInteractions(itemService);
    }

    @SneakyThrows
    @Test
    void updateItem_whenValidItem_thenReturnItem() {

        Long itemId = 1L;

        when(itemService.updateItemDto(any(ItemShortDto.class), anyLong(), anyLong())).thenReturn(itemShortDto);


        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemShortDto), result);
    }

    @SneakyThrows
    @Test
    void updateItem_whenItemDescriptionIsLargerThanMax_thenReturnBadRequest() {

        Long itemId = 1L;
        ItemShortDto itemShortDto1 = itemRequest;

        itemShortDto1.setDescription("Ваш проект ShareIt уже работает и приносит реальную пользу. " +
                "Осталось совсем немного: " +
                "добавить возможность создавать запрос вещи и добавлять вещи в ответ на запросы других пользователей. " +
                "Тогда приложение станет максимально функциональным и удобным.\n" +
                "Но это не всё, что вам предстоит сделать в этом спринте. " +
                "Также нужно будет реализовать одно небольшое, но важное для пользователей улучшение, " +
                "о котором они очень просили." +
                " А ещё перед вами задача: применить полученные в этом спринте знания " +
                "и реализовать тесты для всего приложения.");


        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verifyNoInteractions(itemService);
    }

    @SneakyThrows
    @Test
    void getItem_whenValidId_thenReturnItem() {

        Long itemId = 1L;
        when(itemService.getItemDtoById(anyLong(), anyLong())).thenReturn(itemResponseDto);

        String result = mockMvc.perform(get("/items/{itemId}", itemId)
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemResponseDto), result);
    }

    @SneakyThrows
    @Test
    void getItemsByOwner_whenItemListIsEmpty_thenReturnEmptyList() {

        List<ItemResponseDto> items = List.of();

        when(itemService.getAllOwnerItems(anyLong(), anyInt(), anyInt())).thenReturn(items);


        String result = mockMvc.perform(get("/items?from=0&size=10")
                        .content(objectMapper.writeValueAsString(items))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(items), result);
    }


    @SneakyThrows
    @Test
    void getItemsByOwner_whenItemListIsNotEmpty_thenReturnList() {

        List<ItemResponseDto> items = List.of(itemResponseDto);

        when(itemService.getAllOwnerItems(anyLong(), anyInt(), anyInt())).thenReturn(items);

        String result = mockMvc.perform(get("/items?from=0&size=10")
                        .content(objectMapper.writeValueAsString(items))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(items), result);
    }

    @SneakyThrows
    @Test
    void searchItems_whenItemList_thenReturnList() {

        List<ItemShortDto> items = List.of(itemShortDto);

        when(itemService.getItemsBySearchQuery(anyString(), anyInt(), anyInt())).thenReturn(items);


        String result = mockMvc.perform(get("/items/search?text=вещь&from=0&size=10")
                        .content(objectMapper.writeValueAsString(items))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(items), result);
    }

    @SneakyThrows
    @Test
    void searchItems_whenItemListIsEmpty_thenReturnEmptyList() {

        List<ItemShortDto> items = List.of();

        when(itemService.getItemsBySearchQuery(anyString(), anyInt(), anyInt())).thenReturn(items);

        String result = mockMvc.perform(get("/items/search?text=вещь&from=0&size=10")
                        .content(objectMapper.writeValueAsString(items))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(items), result);
    }

    @SneakyThrows
    @Test
    void createComment_whenValidComment_thenReturnComment() {

        Long itemId = 1L;

        CommentDto commentResponse = CommentDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .text("comment")
                .authorName("owner")
                .build();

        CommentShortDto commentRequest = CommentShortDto.builder()
                .text("short comment")
                .build();

        when(itemService.createComment(anyLong(), anyLong(), any(CommentShortDto.class))).thenReturn(commentResponse);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentResponse.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentResponse.getText())))
                .andExpect(jsonPath("$.authorName", is(commentResponse.getAuthorName())))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @SneakyThrows
    @Test
    void createComment_whenCommentIsNull_thenReturnBadRequest() {

        Long itemId = 1L;

        CommentShortDto commentRequest = CommentShortDto.builder()
                .text(null)
                .build();

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verifyNoInteractions(itemService);
    }
}
