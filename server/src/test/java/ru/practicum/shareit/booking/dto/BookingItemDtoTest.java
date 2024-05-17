package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingItemDtoTest {

    @Autowired
    private JacksonTester<BookingItemDto> json;

    @Test
    void testBookingForItemDto() throws Exception {
        BookingItemDto dto = BookingItemDto.builder()
                .id(1L)
                .bookerId(1L)
                .build();

        JsonContent<BookingItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }
}