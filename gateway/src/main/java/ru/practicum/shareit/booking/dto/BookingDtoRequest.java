package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.StartBeforeEndDateValid;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@StartBeforeEndDateValid
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BookingDtoRequest {

    @FutureOrPresent
    private LocalDateTime start;

    private LocalDateTime end;

    @NotNull
    private Long itemId;
}
