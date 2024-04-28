package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ItemForRequestDto;


import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {


    List<Item> findAllItemsByOwnerId(Long userId, Pageable pageable);

    @Query("select i from Item i where lower(i.name) like lower(concat('%', :search, '%')) " +
            " or lower(i.description) like lower(concat('%', :search, '%')) " +
            " and i.available = true")
    List<Item> getItemsBySearchQuery(@Param("search") String text, Pageable pageable);

    @Query("select i from Item i where i.id =?1 and i.owner.id =?2")
    Optional<Item> findByIdWithUser(Long itemId, Long ownerId);

    List<ItemForRequestDto> findAllByRequestIdIn(List<Long> requestIds);

    List<ItemForRequestDto> findAllByRequestId(Long requestId);
}
