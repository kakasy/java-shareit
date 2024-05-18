package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ItemForRequestDto;


import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllItemsByOwnerId(Long userId, Pageable pageable);

    @Query("select i from Item i where lower(i.name) like lower(concat('%', :search, '%')) " +
            " or lower(i.description) like lower(concat('%', :search, '%')) " +
            " and i.available = true")
    List<Item> search(@Param("search") String text, Pageable pageable);

    List<ItemForRequestDto> findAllByRequestIdIn(Set<Long> requestIds);

    List<ItemForRequestDto> findAllByRequestId(Long requestId);
}
