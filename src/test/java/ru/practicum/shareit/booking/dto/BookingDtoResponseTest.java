package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoResponseTest {

    @Autowired
    private JacksonTester<BookingDtoResponse> json;

    @Test
    void testBookingDtoResponse() throws Exception {
        BookingDtoResponse dto = BookingDtoResponse.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .item(ItemBookingDto.builder()
                        .id(1L)
                        .name("item")
                        .build())
                .booker(UserBookingDto.builder()
                        .id(1L)
                        .build())
                .start(LocalDateTime.of(2020, 1, 1, 11, 0, 0))
                .end(LocalDateTime.of(2020, 1, 3, 12, 20, 0))
                .build();

        JsonContent<BookingDtoResponse> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo("2020-01-01T11:00:00");
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo("2020-01-03T12:20:00");
    }
}
