package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserDto {

    private Long id;

    //@NotBlank(groups = {Create.class})
    //@Size(groups = {Create.class, Update.class}, max = 255)
    private String name;

//    @NotBlank(groups = {Create.class})
//    @Email(message = "Введен некорректный адрес", groups = {Create.class, Update.class})
//    @Size(groups = {Create.class, Update.class}, max = 255)
    private String email;
}