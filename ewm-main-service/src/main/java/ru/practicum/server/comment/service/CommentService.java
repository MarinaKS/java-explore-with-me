package ru.practicum.server.comment.service;

import ru.practicum.server.comment.dto.CommentDto;
import ru.practicum.server.comment.dto.CommentNewDto;
import ru.practicum.server.comment.dto.CommentUpdateDto;

import java.util.List;

public interface CommentService {
    CommentDto addComment(CommentNewDto commentNewDto, Long userId, Long eventId);

    CommentDto updateCommentByUser(CommentUpdateDto commentUpdateDto, Long userId, Long commentId);

    void deleteCommentByUser(Long userId, Long commentId);

    void deleteCommentByAdmin(Long commentId);

    CommentDto updateCommentByAdmin(CommentUpdateDto commentUpdateDto, Long commentId);

    List<CommentDto> getCommentsByAdmin(String text, Long userId, String start, String end, Integer from, Integer size);

    List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size);
}
