package ru.practicum.server.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.server.event.service.EventService;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping
public class EventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> findEventsByUser(@RequestParam(name = "text", required = false) String text,
                                               @RequestParam(name = "categories", required = false) List<Long> categories,
                                               @RequestParam(name = "paid", required = false) Boolean paid,
                                               @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                               @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                               @RequestParam(name = "onlyAvailable", required = false) boolean onlyAvailable,
                                               @RequestParam(name = "sort", required = false) SortValue sort,
                                               @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                               @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                               HttpServletRequest request) {
        return eventService.findEventsByUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) {
        return eventService.getEvent(id, request);
    }
}
