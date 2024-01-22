package ru.practicum.server.event.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.server.category.mapper.CategoryMapper;
import ru.practicum.server.category.model.Category;
import ru.practicum.server.event.dto.EventFullDto;
import ru.practicum.server.event.dto.EventNewDto;
import ru.practicum.server.event.dto.EventShortDto;
import ru.practicum.server.event.model.Event;
import ru.practicum.server.event.model.EventState;
import ru.practicum.server.user.mapper.UserMapper;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EventMapper {
    public static EventFullDto toEventFullDto(Event event, Long views, Long confirmedRequests) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getEventState())
                .title(event.getTitle())
                .views(views)
                .build();
    }

    public static Event toEvent(EventNewDto eventNewDto, Category category, User initiator) {
        Event event = new Event();
        event.setAnnotation(eventNewDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(eventNewDto.getDescription());
        event.setEventDate(LocalDateTime.parse(eventNewDto.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        event.setLocation(eventNewDto.getLocation());
        event.setPaid(eventNewDto.isPaid());
        event.setParticipantLimit(eventNewDto.getParticipantLimit());
        event.setTitle(eventNewDto.getTitle());
        event.setInitiator(initiator);
        event.setCreatedOn(LocalDateTime.now());
        event.setRequestModeration(eventNewDto.getRequestModeration());
        event.setEventState(EventState.PENDING);
        return event;
    }

    public static EventShortDto toEventShortDto(Event event, Long views, Long confirmedRequests) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .build();
    }
}
