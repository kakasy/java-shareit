package ru.practicum.shareit.item.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class CommentShortDto {

    @Size(max = 512)
    @NotBlank
    private String text;
}
