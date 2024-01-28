package ru.practicum.server.comment.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.server.comment.dto.CommentDto;
import ru.practicum.server.comment.dto.CommentNewDto;
import ru.practicum.server.comment.model.Comment;
import ru.practicum.server.event.model.Event;
import ru.practicum.server.user.mapper.UserMapper;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;

@Service
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getCreated(),
                UserMapper.toUserShortDto(comment.getAuthor()),
                comment.getEvent().getId(),
                comment.getModifiedByAdmin(),
                comment.getModifiedByAuthor());
    }

    public static Comment toComment(CommentNewDto commentNewDto,
                                    User user,
                                    Event event,
                                    LocalDateTime created) {
        Comment comment = new Comment();
        comment.setText(commentNewDto.getText());
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setCreated(created);
        return comment;
    }

}
