package ru.practicum.shareit.item.dto;

public interface ItemForRequestDto {
    Long getId();

    String getName();

    String getDescription();

    Boolean getAvailable();

    Long getRequestId();
}