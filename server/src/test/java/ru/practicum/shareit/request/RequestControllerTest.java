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

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {

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