package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
    private List<CommentDto> comments;
}
