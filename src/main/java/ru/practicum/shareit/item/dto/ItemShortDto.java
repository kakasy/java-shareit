package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ItemShortDto {

    private Long id;


    @Size(groups = {Create.class, Update.class}, max = 255)
    @NotBlank(groups = Create.class, message = "Имя не может быть пустым")
    private String name;

    @Size(groups = {Create.class, Update.class}, max = 512)
    @NotBlank(groups = Create.class, message = "Описание не может быть пустым")
    private String description;

    @NotNull(groups = Create.class)
    private Boolean available;
}
