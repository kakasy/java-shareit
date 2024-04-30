package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerIT {

    @InjectMocks
    BookingController bookingController;

    @Mock
    BookingServiceImpl bookingService;


    @Autowired
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @Autowired
    private MockMvc mockMvc;

    private BookingDtoRequest bookingDtoRequest;

    private BookingDtoResponse bookingDtoResponse;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void startUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(ItemBookingDto.builder().id(1L).name("item").build())
                .booker(UserBookingDto.builder().id(1L).build())
                .status(BookingStatus.WAITING)
                .build();

        bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(1L)
                .build();
    }


    @SneakyThrows
    @Test
    void createBooking_whenValidBooking_thenReturnBooking() {

        when(bookingService.createBooking(anyLong(), any(BookingDtoRequest.class)))
                .thenReturn(bookingDtoResponse);

        String result = mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoResponse), result);
    }

    @SneakyThrows
    @Test
    void createBooking_whenInvalidBookingStart_thenReturnBadRequest() {

        BookingDtoRequest invalidBookingRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(1L)
                .build();

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(invalidBookingRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingService);
    }

    @SneakyThrows
    @Test
    void approvedBooking_whenStatusApprove_thenReturnBooking()  {

        Long bookingId = 1L;

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDtoResponse);


        String result = mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoResponse), result);
    }

    @SneakyThrows
    @Test
    void getBookingById_whenValidId_thenReturnBooking() {

        Long bookingId = 1L;

        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDtoResponse);


        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoResponse), result);
    }

    @SneakyThrows
    @Test
    void getSortBookingByUser_whenValidSortState_thenReturnBookingList() {

        List<BookingDtoResponse> responseList = List.of(bookingDtoResponse);

        when(bookingService.getBookingsByCurrentUser(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(responseList);

        String result = mockMvc.perform(get("/bookings?state=ALL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseList), result);
    }
}
