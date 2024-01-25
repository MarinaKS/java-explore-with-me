package ru.practicum.server.compilation.model;

import lombok.Data;
import ru.practicum.server.event.model.Event;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "compilation", schema = "public")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean pinned;
    private String title;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "compilation_event",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    @OrderBy("eventDate")
    private List<Event> events;
}
