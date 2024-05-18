package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemId(Long item);

    Set<Comment> findAllByItemIdIn(Set<Long> itemId);
}