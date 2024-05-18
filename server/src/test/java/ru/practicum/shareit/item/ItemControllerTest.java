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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

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

        when(itemService.createItem(any(ItemShortDto.class), anyLong())).thenReturn(itemShortDto);

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
    void updateItem_whenValidItem_thenReturnItem() {

        Long itemId = 1L;

        when(itemService.updateItem(any(ItemShortDto.class), anyLong(), anyLong())).thenReturn(itemShortDto);


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
    void getItem_whenValidId_thenReturnItem() {

        Long itemId = 1L;
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemResponseDto);

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

        when(itemService.getItemsByUser(anyLong(), anyInt(), anyInt())).thenReturn(items);


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

        when(itemService.getItemsByUser(anyLong(), anyInt(), anyInt())).thenReturn(items);

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
}
