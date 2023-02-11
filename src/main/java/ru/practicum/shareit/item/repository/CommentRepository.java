package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c where c.item.id = ?1 ")
    List<Comment> findByItemId(long itemId);

    @Query("select c from  Comment c where c.author.id = ?1")
    List<CommentDto> findByUserId(long userId);
}
