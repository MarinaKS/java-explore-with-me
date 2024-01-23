package ru.practicum.server.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.server.event.model.Location;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class EventNewDto {
    @NotNull
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotNull
    private Long category;
    @NotNull
    @Size(min = 20, max = 7000)
    private String description;
    @NotNull
    private String eventDate;
    @NotNull
    private Location location;
    private boolean paid;
    private int participantLimit;
    private Boolean requestModeration;
    @NotNull
    @Size(min = 3, max = 120)
    private String title;
}
