package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Data
@AllArgsConstructor
public class User {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email(message = "Введен некорректный адрес")
    private String email;

}
