package ru.practicum.shareit.booking;


import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

@UtilityClass
public class BookingMapper {

    public BookingDtoResponse toBookingDtoResponse(Booking booking) {
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemBookingDto(booking.getItem()))
                .booker(UserMapper.toUserBookingDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public Booking toBooking(BookingDtoRequest bookingDto, Item item, User user, BookingStatus status) {

        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(user)
                .status(status)
                .build();
    }

    public BookingItemDto toBookingItemDto(Booking booking) {

        if (booking == null) {
            return null;
        }

        return BookingItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

}