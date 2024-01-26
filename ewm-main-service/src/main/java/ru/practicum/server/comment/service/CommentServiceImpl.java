package ru.practicum.server.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.comment.dto.CommentDto;
import ru.practicum.server.comment.dto.CommentNewDto;
import ru.practicum.server.comment.dto.CommentUpdateDto;
import ru.practicum.server.comment.mapper.CommentMapper;
import ru.practicum.server.comment.model.Comment;
import ru.practicum.server.comment.repository.CommentRepository;
import ru.practicum.server.event.model.Event;
import ru.practicum.server.event.repository.EventRepository;
import ru.practicum.server.exception.ConflictException;
import ru.practicum.server.exception.ObjectNotFoundException;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String RANGE_START = "2022-01-01 23:59:59";
    private static final String RANGE_END = "2099-01-01 23:59:59";

    @Transactional
    @Override
    public CommentDto addComment(CommentNewDto commentNewDto, Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с таким id не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Такое событие не найдено"));
        return CommentMapper.toCommentDto(
                commentRepository.save(CommentMapper.toComment(commentNewDto, user, event, LocalDateTime.now()))
        );
    }

    @Transactional
    @Override
    public CommentDto updateCommentByUser(CommentUpdateDto commentUpdateDto, Long userId, Long commentId) {
        LocalDateTime now = LocalDateTime.now();
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с таким id не найден"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ObjectNotFoundException("Такой комментарий не существует"));
        eventRepository.findById(comment.getEvent().getId())
                .orElseThrow(() -> new ObjectNotFoundException("Такое событие не найдено"));
        if (!userId.equals(comment.getAuthor().getId())) {
            throw new ConflictException("Редактировать комментарий может только его создатель");
        }
        comment.setText(commentUpdateDto.getText());
        comment.setModifiedByAuthor(now);
        Comment commentUpdated = commentRepository.save(comment);
        return CommentMapper.toCommentDto(commentUpdated);
    }

    @Transactional
    @Override
    public void deleteCommentByUser(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ObjectNotFoundException("Такой комментарий не существует"));
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с таким id не найден"));
        if (!userId.equals(comment.getAuthor().getId())) {
            throw new ConflictException("Удалить комментарий может только его создатель");
        }
        commentRepository.delete(comment);
    }

    @Transactional
    @Override
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ObjectNotFoundException("Такой комментарий не существует"));
        commentRepository.delete(comment);
    }

    @Override
    public CommentDto updateCommentByAdmin(CommentUpdateDto commentUpdateDto, Long commentId) {
        LocalDateTime now = LocalDateTime.now();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ObjectNotFoundException("Такой комментарий не существует"));
        comment.setText(commentUpdateDto.getText());
        comment.setModifiedByAdmin(now);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByAdmin(
            String text,
            Long userId,
            String start,
            String end,
            Integer from,
            Integer size
    ) {
        Pageable page = PageRequest.of(from / size, size);
        if (text == null) text = "";
        LocalDateTime startParsed = null;
        LocalDateTime endParsed = null;
        if (start != null) {
            startParsed = LocalDateTime.parse(start, dateTimeFormatter);
        }
        if (end != null) {
            endParsed = LocalDateTime.parse(end, dateTimeFormatter);
        }
        if (startParsed != null && endParsed != null && startParsed.isAfter(endParsed)) {
            throw new ValidationException("Начало позже конца временного промежутка");
        }
        List<Comment> comments = commentRepository.findAllByParams(userId, text, startParsed, endParsed, page);
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Такое событие не найдено"));
        Pageable page = PageRequest.of(from / size, size);
        List<CommentDto> comments = commentRepository.findAllByEventId(eventId, page)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        return comments;
    }
}
