package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoRequestTest {

    @Autowired
    private JacksonTester<BookingDtoRequest> json;

    @SneakyThrows
    @Test
    void testBookingDtoRequest() {
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.of(2020, 1, 1, 11, 11, 0))
                .end(LocalDateTime.of(2020, 1, 3, 12, 20, 0))
                .itemId(1L)
                .build();

        JsonContent<BookingDtoRequest> result = json.write(bookingDtoRequest);

        assertThat(result).extractingJsonPathValue("$.start").isEqualTo("2020-01-01T11:11:00");
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo("2020-01-03T12:20:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }
}
