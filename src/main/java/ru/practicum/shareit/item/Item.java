package ru.practicum.shareit.item;

import lombok.*;
import org.hibernate.annotations.JoinFormula;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.User;

import javax.persistence.*;


@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor
@Table(name="items")
public class Item {

    @Id
    @Column(name="id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="description", nullable = false)
    private String description;

    @Column(name="available", nullable = false)
    private Boolean available;

    @ManyToOne
    @JoinColumn(name="owner_id")
    private User owner;

    @Column(name="request_id")
    private Long requestId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(SELECT b.id FROM bookings b " +
            " WHERE b.item_id = id " +
            " AND b.start_date > LOCALTIMESTAMP(2) " +
            " AND b.status = 'APPROVED' " +
            " ORDER BY b.start_date ASC LIMIT 1)")
    private Booking nextBooking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(SELECT b.id FROM bookings b " +
            " WHERE b.item_id = id " +
            " AND b.start_date <= LOCALTIMESTAMP(2) " +
            " AND b.status = 'APPROVED' " +
            " ORDER BY b.end_date DESC LIMIT 1)")
    private Booking lastBooking;

}
