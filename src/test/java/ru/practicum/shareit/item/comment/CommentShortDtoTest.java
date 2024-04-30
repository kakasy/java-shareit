package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.dto.CommentShortDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentShortDtoTest {

    @Autowired
    private JacksonTester<CommentShortDto> json;

    @Test
    void testCommentDtoRequest() throws Exception {
        CommentShortDto dto = CommentShortDto.builder()
                .text("comment")
                .build();

        JsonContent<CommentShortDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("comment");
    }
}
