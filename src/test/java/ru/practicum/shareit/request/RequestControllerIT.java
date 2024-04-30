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
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RequestControllerIT {

    @Mock
    private RequestServiceImpl itemRequestService;
    @InjectMocks
    private ItemRequestController itemRequestController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ItemRequestResponseDto responseDto;

    private static final String USER_HEADER = "X-Sharer-User-Id";
    
    @BeforeEach
    void startUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();

        responseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .items(List.of())
                .created(LocalDateTime.now())
                .description("description")
                .requesterId(1L)
                .build();
    }


    @SneakyThrows
    @Test
    void createItemRequest_whenValidItemRequest_thenReturnItemRequest() {

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("description")
                .build();

        when(itemRequestService.createRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void createItemRequest_whenEmptyItemRequest_thenReturnBadRequest() {

        ItemRequestDto invalidRequest = ItemRequestDto.builder()
                .description("")
                .build();

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }

    @SneakyThrows
    @Test
    void createItemRequest_whenInvalidItemRequestDescription_thenReturnBadRequest() {

        ItemRequestDto invalidRequest = ItemRequestDto.builder()
                .description("Ваш проект ShareIt уже работает и приносит реальную пользу. " +
                        "Осталось совсем немного: добавить возможность создавать запрос вещи и добавлять вещи " +
                        "в ответ на запросы других пользователей. " +
                        "Тогда приложение станет максимально функциональным и удобным.\n" +
                        "Но это не всё, что вам предстоит сделать в этом спринте. " +
                        "Также нужно будет реализовать одно небольшое, " +
                        "но важное для пользователей улучшение, о котором они очень просили. " +
                        "А ещё перед вами задача: применить полученные в этом спринте " +
                        "знания и реализовать тесты для всего приложения.")
                .build();

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }

    @SneakyThrows
    @Test
    void getRequestsById_whenValidId_thenReturnItemRequest() {

        Long requestId = 1L;

        when(itemRequestService.getRequestsById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenEmptyRequestItemList_thenReturnEmptyList() {

        List<ItemRequestResponseDto> itemRequests = List.of();

        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(itemRequests);

        String result = mockMvc.perform(get("/requests/all?from=0&size=10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(itemRequests));

    }

    @SneakyThrows
    @Test
    void getAllRequests_whenRequestItemList_thenReturnList() {

        List<ItemRequestResponseDto> itemRequests = List.of(responseDto);

        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(itemRequests);

        mockMvc.perform(get("/requests/all?from=0&size=10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @SneakyThrows
    @Test
    void getRequests_whenEmptyRequestItemList_thenReturnEmptyList() {

        List<ItemRequestResponseDto> itemRequests = List.of();

        when(itemRequestService.getRequestsByOwner(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemRequests);

        String result = mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(itemRequests));
    }

    @SneakyThrows
    @Test
    void getRequests_whenRequestItemListWithOneEntity_thenReturnList() {

        List<ItemRequestResponseDto> itemRequests = List.of(responseDto);

        when(itemRequestService.getRequestsByOwner(anyLong(), anyInt(), anyInt())).thenReturn(itemRequests);

        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
