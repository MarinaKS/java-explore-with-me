package ru.practicum.server.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.event.dto.*;
import ru.practicum.server.event.model.EventState;
import ru.practicum.server.event.service.EventService;
import ru.practicum.server.request.dto.RequestEventStatusUpdateRequest;
import ru.practicum.server.request.dto.RequestEventStatusUpdateResult;
import ru.practicum.server.request.dto.RequestParticipationDto;
import ru.practicum.server.request.service.RequestService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping
public class EventController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping("/admin/events")
    public List<EventFullDto> getEventsByAdmin(@RequestParam(name = "users", required = false) List<Long> users,
                                               @RequestParam(name = "states", required = false) List<EventState> states,
                                               @RequestParam(name = "categories", required = false) List<Long> categoriesId,
                                               @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                               @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                               @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                               @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return eventService.getEventsByAdmin(users, states, categoriesId, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable(name = "eventId") Long eventId,
                                           @Valid @RequestBody EventUpdateAdminRequestDto eventUpdateAdminRequestDto) {
        return eventService.updateEventByAdmin(eventId, eventUpdateAdminRequestDto);

    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getEventsByInitiator(@PathVariable Long userId,
                                                    @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        return eventService.getEventsByInitiator(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @Valid @RequestBody EventNewDto eventNewDto) {
        return eventService.addEvent(userId, eventNewDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getEventByInitiator(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEventByInitiator(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEventByInitiator(@PathVariable Long userId,
                                               @PathVariable Long eventId,
                                               @Valid @RequestBody EventUpdateUserRequestDto eventUpdateUserRequestDto) {
        return eventService.updateEventByInitiator(userId, eventId, eventUpdateUserRequestDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<RequestParticipationDto> getRequestsByEventIdAndInitiatorId(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.getRequestsByEventIdAndInitiatorId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public RequestEventStatusUpdateResult updateRequest(@PathVariable Long userId,
                                                        @PathVariable Long eventId,
                                                        @RequestBody RequestEventStatusUpdateRequest requestStatusUpdateDto) {
        return requestService.updateRequest(userId, eventId, requestStatusUpdateDto);
    }

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam(name = "text", required = false) String text,
                                        @RequestParam(name = "categories", required = false) List<Long> categories,
                                        @RequestParam(name = "paid", required = false) Boolean paid,
                                        @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                        @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                        @RequestParam(name = "onlyAvailable", required = false) boolean onlyAvailable,
                                        @RequestParam(name = "sort", required = false) SortValue sort,
                                        @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                        @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                        HttpServletRequest request) {
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEventByEventId(@PathVariable Long id, HttpServletRequest request) {
        return eventService.getEvent(id, request);
    }
}
