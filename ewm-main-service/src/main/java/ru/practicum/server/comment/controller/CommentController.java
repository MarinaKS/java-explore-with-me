package ru.practicum.server.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.comment.dto.CommentDto;
import ru.practicum.server.comment.dto.CommentNewDto;
import ru.practicum.server.comment.dto.CommentUpdateDto;
import ru.practicum.server.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/users/{userId}/comments/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@RequestBody @Valid CommentNewDto commentNewDto,
                                 @PathVariable Long userId,
                                 @PathVariable Long eventId) {
        return commentService.addComment(commentNewDto, userId, eventId);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    public CommentDto updateCommentByUser(@RequestBody @Valid CommentUpdateDto commentUpdateDto,
                                          @PathVariable Long userId,
                                          @PathVariable Long commentId) {
        return commentService.updateCommentByUser(commentUpdateDto, userId, commentId);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(@PathVariable Long userId,
                                    @PathVariable Long commentId) {
        commentService.deleteCommentByUser(userId, commentId);
    }

    @DeleteMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }

    @PatchMapping("/admin/comments/{commentId}")
    public CommentDto updateCommentByAdmin(@RequestBody @Valid CommentUpdateDto commentUpdateDto,
                                           @PathVariable Long commentId) {
        return commentService.updateCommentByAdmin(commentUpdateDto, commentId);
    }

    @GetMapping("/admin/comments")
    public List<CommentDto> getCommentsByAdmin(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) Long userId,
                                               @RequestParam(defaultValue = "0", required = false) Integer from,
                                               @RequestParam(defaultValue = "10", required = false) Integer size,
                                               @RequestParam(required = false) String start,
                                               @RequestParam(required = false) String end) {
        return commentService.getCommentsByAdmin(text, userId, start, end, from, size);
    }

    @GetMapping("/comments")
    public List<CommentDto> getCommentsByEventId(@Positive @RequestParam(value = "eventId") Long eventId,
                                                 @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return commentService.getCommentsByEventId(eventId, from, size);
    }
}
