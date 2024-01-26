package ru.practicum.server.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.server.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    @Query("select c from Comment c " +
            "where (:query is null or lower(c.text) like lower(concat('%', :query, '%'))) " +
            "and (:userId is null or c.author.id = :userId) " +
            "and (CAST(:startParsed AS timestamp) is null or c.created >= :startParsed) " +
            "and (CAST(:endParsed AS timestamp) is null or c.created <= :endParsed) " +
            "order by c.created desc")
    List<Comment> findAllByParams(Long userId,
                                  String query,
                                  LocalDateTime startParsed,
                                  LocalDateTime endParsed,
                                  Pageable page);

    @Query("select c from Comment c " +
            "where c.event.id = :eventId " +
            "order by c.created desc")
    List<Comment> findAllByEventId(Long eventId, Pageable page);
}
