package ru.practicum.server.comment.model;

import lombok.Data;
import ru.practicum.server.event.model.Event;
import ru.practicum.server.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comment", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @Column(name = "created")
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @Column(name = "modified_by_admin")
    private LocalDateTime modifiedByAdmin;
    @Column(name = "modified_by_author")
    private LocalDateTime modifiedByAuthor;
}
