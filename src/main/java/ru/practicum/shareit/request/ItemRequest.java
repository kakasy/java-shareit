package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ItemRequest {

    private Long id;
    private String description;
    private Long requestorId;
    private LocalDate created;
}
