package ru.practicum.shareit.request;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorId(Long requesterId, Pageable pageable);

    List<ItemRequest> findAllByRequestorIdNot(Long requesterId, Pageable pageable);
}
