package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {


    List<Item> findAllItemsByOwnerId(Long userId);

    @Query("select i from Item i where lower(i.name) like lower(concat('%', :search, '%')) " +
            " or lower(i.description) like lower(concat('%', :search, '%')) " +
            " and i.available = true")
    List<Item> getItemsBySearchQuery(@Param("search") String text);

    @Query("select i from Item i where i.id =?1 and i.owner.id =?2")
    Optional<Item> findByIdWithUser(Long itemId, Long ownerId);
}
