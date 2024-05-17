package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserBookingDtoTest {
    @Autowired
    private JacksonTester<UserBookingDto> json;

    @SneakyThrows
    @Test
    void testUserDtoForBooking() {
        UserBookingDto dto = UserBookingDto.builder()
                .id(1L)
                .build();

        JsonContent<UserBookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }
}