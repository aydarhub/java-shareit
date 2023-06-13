package ru.practicum.shareit.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.comment.model.Comment;

import java.util.Collection;
import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemId(Long itemId);

    Collection<Comment> findAllByItemIdIn(List<Long> itemIds);

}
