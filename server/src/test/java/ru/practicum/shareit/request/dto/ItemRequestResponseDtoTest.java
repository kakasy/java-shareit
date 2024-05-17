package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestResponseDtoTest {

    @Autowired
    private JacksonTester<ItemRequestResponseDto> json;

    @Test
    void testItemRequestDtoResponse() throws Exception {
        ItemRequestResponseDto dto = ItemRequestResponseDto.builder()
                .id(1L)
                .items(List.of())
                .created(LocalDateTime.of(2020, 1, 1, 11, 0, 0))
                .description("description")
                .requesterId(1L)
                .build();

        JsonContent<ItemRequestResponseDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathArrayValue("$.items").isEqualTo(List.of());
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo("2020-01-01T11:00:00");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.requesterId").isEqualTo(1);
    }
}
