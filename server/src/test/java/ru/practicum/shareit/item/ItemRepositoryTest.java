package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.pagination.Pagination;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void startUp() {

        User user = User.builder()
                .id(1L)
                .name("name")
                .email("user@mail.ru")
                .build();

        userRepository.save(user);

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("item description")
                .owner(user)
                .available(true)
                .requestId(null)
                .lastBooking(null)
                .nextBooking(null)
                .build();

        itemRepository.save(item);
    }

    @Test
    void search() {
        List<Item> actualItems = itemRepository.getItemsBySearchQuery("item",
                Pagination.withoutSort(0, 10));

        assertEquals(1, actualItems.size());
        assertEquals("item description", actualItems.get(0).getDescription());
    }

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}
