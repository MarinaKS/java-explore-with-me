package ru.practicum.server.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.server.event.model.Location;

import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class EventUpdateUserRequestDto {
    @Size(min = 1, max = 500)
    private String annotation;
    private Long category;
    @Size(min = 1, max = 2000)
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration;
    private UserStateAction stateAction;
    @Size(min = 1, max = 120)
    private String title;
}
