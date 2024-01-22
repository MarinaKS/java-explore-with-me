package ru.practicum.server.event.service;

import ru.practicum.server.event.dto.*;
import ru.practicum.server.event.model.EventState;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    List<EventFullDto> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categoriesId, String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long eventId, EventUpdateAdminRequestDto eventUpdateAdminRequestDto);

    EventFullDto addEvent(Long userId, EventNewDto eventNewDto);

    List<EventShortDto> getEventsByInitiator(Long userId, Integer from, Integer size);

    EventFullDto getEventByInitiator(Long userId, Long eventId);

    EventFullDto updateEventByInitiator(Long userId, Long eventId, EventUpdateUserRequestDto eventUpdateUserRequestDto);

    List<EventFullDto> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, boolean onlyAvailable, SortValue sort, Integer from, Integer size, HttpServletRequest request);

    EventFullDto getEvent(Long id, HttpServletRequest request);
}
