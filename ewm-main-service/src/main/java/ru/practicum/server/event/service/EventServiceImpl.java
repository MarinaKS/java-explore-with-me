package ru.practicum.server.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.category.model.Category;
import ru.practicum.server.category.repository.CategoryRepository;
import ru.practicum.server.event.dto.*;
import ru.practicum.server.event.mapper.EventMapper;
import ru.practicum.server.event.model.Event;
import ru.practicum.server.event.model.EventState;
import ru.practicum.server.event.repository.EventRepository;
import ru.practicum.server.exception.ConflictException;
import ru.practicum.server.exception.ObjectNotFoundException;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.request.repository.RequestRepository;
import ru.practicum.server.request.service.RequestService;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;
import ru.practicum.server.view.service.ViewService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ViewService viewService;
    private final RequestService requestService;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String RANGE_END = "2099-01-01 23:59:59";

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users,
                                               List<EventState> states,
                                               List<Long> categoriesId,
                                               String rangeStart,
                                               String rangeEnd,
                                               Integer from,
                                               Integer size) {
        List<EventFullDto> eventsDto = new ArrayList<>();
        Pageable page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findEventsByAdmin(
                users, states, categoriesId,
                rangeStart == null ? null : LocalDateTime.parse(rangeStart, dateTimeFormatter),
                rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, dateTimeFormatter),
                page);
        if (events != null) {
            Map<Long, Long> viewsByEventIds = viewService.getViewsByEvents(events);
            Map<Long, Long> confirmedRequestCountsByEventsIds = requestService.getConfirmedRequestCountsByEventsIds(events);
            eventsDto = events.stream()
                    .map(e -> EventMapper.toEventFullDto(
                            e,
                            viewsByEventIds.get(e.getId()),
                            confirmedRequestCountsByEventsIds.get(e.getId())
                    ))
                    .collect(Collectors.toList());
        }
        return eventsDto;
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, EventUpdateAdminRequestDto eventUpdateAdminRequestDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Такой ивент не найден"));
        if (eventUpdateAdminRequestDto.getEventDate() != null) {
            if (LocalDateTime.parse(eventUpdateAdminRequestDto.getEventDate(),
                    dateTimeFormatter).isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ValidationException("Lата начала изменяемого события должна быть не ранее чем за час от даты публикации.");
            } else {
                event.setEventDate(LocalDateTime.parse(eventUpdateAdminRequestDto.getEventDate(),
                        dateTimeFormatter));
            }
        }
        if (eventUpdateAdminRequestDto.getCategory() != null) {
            Category category = categoryRepository.findById(eventUpdateAdminRequestDto.getCategory())
                    .orElseThrow(() -> new ObjectNotFoundException("Нет такой категории"));
            event.setCategory(category);
        }
        if (eventUpdateAdminRequestDto.getAnnotation() != null) {
            event.setAnnotation(eventUpdateAdminRequestDto.getAnnotation());
        }
        if (eventUpdateAdminRequestDto.getDescription() != null) {
            event.setDescription(eventUpdateAdminRequestDto.getDescription());
        }
        if (eventUpdateAdminRequestDto.getLocation() != null) {
            event.setLocation(eventUpdateAdminRequestDto.getLocation());
        }
        if (eventUpdateAdminRequestDto.getPaid() != null) {
            event.setPaid(eventUpdateAdminRequestDto.getPaid());
        }
        if (eventUpdateAdminRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateAdminRequestDto.getParticipantLimit().intValue());
        }
        if (eventUpdateAdminRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateAdminRequestDto.getRequestModeration());
        }
        if (eventUpdateAdminRequestDto.getTitle() != null) {
            event.setTitle(eventUpdateAdminRequestDto.getTitle());
        }
        if (eventUpdateAdminRequestDto.getStateAction() != null) {
            if (eventUpdateAdminRequestDto.getStateAction() == AdminStateAction.PUBLISH_EVENT) {
                if (event.getEventState() == EventState.PUBLISHED) {
                    throw new ConflictException("Событие уже опубликовано");
                }
                if (event.getEventState() == EventState.CANCELED) {
                    throw new ConflictException("Событие уже отклонено");
                }
                event.setEventState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (eventUpdateAdminRequestDto.getStateAction() == AdminStateAction.REJECT_EVENT) {
                if (event.getEventState() == EventState.PUBLISHED) {
                    throw new ConflictException("Событие уже опубликовано");
                }
                event.setEventState(EventState.CANCELED);
            }
        }
        Long views = viewService.getViewsByEvents(List.of(event)).get(eventId);
        Long confirmedRequest = requestService.getConfirmedRequestCountsByEventsIds(List.of(event)).get(eventId);

        return EventMapper.toEventFullDto(eventRepository.save(event), views, confirmedRequest);
    }

    @Transactional
    @Override
    public EventFullDto addEvent(Long userId, EventNewDto eventNewDto) {
        if (LocalDateTime.now().plusHours(2).isAfter(LocalDateTime.parse(eventNewDto.getEventDate(), dateTimeFormatter))) {
            throw new ValidationException("Старт события должен быть не раньше, чем через 2 часа");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с таким d не найден"));
        Category category = categoryRepository.findById(eventNewDto.getCategory())
                .orElseThrow(() -> new ObjectNotFoundException("Такая категория не найдена"));
        Event event = EventMapper.toEvent(eventNewDto, category, user);
        event = eventRepository.save(event);
        Long views = viewService.getViewsByEvents(List.of(event)).get(event.getId());
        Long confirmedRequest = requestService.getConfirmedRequestCountsByEventsIds(List.of(event)).get(event.getId());

        return EventMapper.toEventFullDto(eventRepository.save(event), views, confirmedRequest);
    }

    @Override
    public List<EventShortDto> getEventsByInitiator(Long userId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, page);
        Map<Long, Long> confirmedRequestsByEventsIds = requestService.getConfirmedRequestCountsByEventsIds(events);
        Map<Long, Long> viewsByEventIds = viewService.getViewsByEvents(events);
        return events.stream()
                .map(event -> EventMapper.toEventShortDto(event,
                        viewsByEventIds.get(event.getId()),
                        confirmedRequestsByEventsIds.get(event.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto getEventByInitiator(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Такой ивент не найден"));
        Long views = viewService.getViewsByEvents(List.of(event)).get(eventId);
        Long confirmedRequest = requestService.getConfirmedRequestCountsByEventsIds(List.of(event)).get(eventId);
        return EventMapper.toEventFullDto(event, views, confirmedRequest);
    }

    @Transactional
    @Override
    public EventFullDto updateEventByInitiator(Long userId, Long eventId, EventUpdateUserRequestDto eventUpdateUserRequestDto) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new ObjectNotFoundException("Такой ивент не найден"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ObjectNotFoundException("Изменять событие может только создатель события");
        }
        if (eventUpdateUserRequestDto.getEventDate() != null) {
            if (LocalDateTime.parse(eventUpdateUserRequestDto.getEventDate(),
                    dateTimeFormatter).isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
            } else {
                event.setEventDate(LocalDateTime.parse(eventUpdateUserRequestDto.getEventDate(),
                        dateTimeFormatter));
            }
        }
        if (eventUpdateUserRequestDto.getCategory() != null) {
            Category category = categoryRepository.findById(eventUpdateUserRequestDto.getCategory())
                    .orElseThrow(() -> new ObjectNotFoundException("Такая категория не найдена"));
            event.setCategory(category);
        }
        if (event.getEventState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя изменить опубликованное событие");
        }
        if (eventUpdateUserRequestDto.getAnnotation() != null) {
            event.setAnnotation(eventUpdateUserRequestDto.getAnnotation());
        }
        if (eventUpdateUserRequestDto.getDescription() != null) {
            event.setDescription(eventUpdateUserRequestDto.getDescription());
        }
        if (eventUpdateUserRequestDto.getLocation() != null) {
            event.setLocation(eventUpdateUserRequestDto.getLocation());
        }
        if (eventUpdateUserRequestDto.getPaid() != null) {
            event.setPaid(eventUpdateUserRequestDto.getPaid());
        }
        if (eventUpdateUserRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateUserRequestDto.getParticipantLimit().intValue());
        }
        if (eventUpdateUserRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateUserRequestDto.getRequestModeration());
        }
        if (eventUpdateUserRequestDto.getTitle() != null) {
            event.setTitle(eventUpdateUserRequestDto.getTitle());
        }
        if (eventUpdateUserRequestDto.getStateAction() != null) {
            if (eventUpdateUserRequestDto.getStateAction() == UserStateAction.SEND_TO_REVIEW) {
                event.setEventState(EventState.PENDING);
            } else {
                event.setEventState(EventState.CANCELED);
            }
        }

        Long views = viewService.getViewsByEvents(List.of(event)).get(event.getId());
        Long confirmedRequest = requestService.getConfirmedRequestCountsByEventsIds(List.of(event)).get(event.getId());
        return EventMapper.toEventFullDto(eventRepository.save(event), views, confirmedRequest);
    }

    @Override
    public List<EventFullDto> getEvents(String text,
                                        List<Long> categories,
                                        Boolean paid,
                                        String rangeStart,
                                        String rangeEnd,
                                        boolean onlyAvailable,
                                        SortValue sort,
                                        Integer from,
                                        Integer size,
                                        HttpServletRequest request) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.parse(RANGE_END, dateTimeFormatter);
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, dateTimeFormatter);
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
        }
        if (start.isAfter(end)) {
            throw new ValidationException("Начало позже конца временного промежутка");
        }
        if (text == null) text = "";
        List<Event> eventsPage;
        Map<Long, Long> views;
        if (sort == null || sort.equals(SortValue.EVENT_DATE)) {
            PageRequest pageable = PageRequest.of(from / size, size);
            eventsPage = eventRepository.findByParamsOrderByDate(text.toLowerCase(), List.of(EventState.PUBLISHED),
                    categories, paid, start, end, onlyAvailable, pageable);
            views = viewService.getViewsByEvents(eventsPage);
        } else {
            PageRequest pageable = PageRequest.of(0, Integer.MAX_VALUE);
            List<Event> eventsAll = eventRepository.findByParamsOrderByDate(text.toLowerCase(), List.of(EventState.PUBLISHED),
                    categories, paid, start, end, onlyAvailable, pageable);
            Map<Long, Event> eventsAllById = eventsAll.stream().collect(Collectors.toMap(Event::getId, Function.identity()));
            views = viewService.getViewsByEvents(eventsAll);
            eventsPage = views.entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
                    .skip(from).limit(size).map(e -> eventsAllById.get(e.getKey())).collect(Collectors.toList());
        }
        Map<Long, Long> confirmedRequests = requestService.getConfirmedRequestCountsByEventsIds(eventsPage);

        viewService.sendHit(request);
        eventsPage.forEach(e -> viewService.sendHit(request, "/events/" + e.getId()));
        return eventsPage.stream()
                .map(e -> EventMapper.toEventFullDto(e, views.get(e.getId()), confirmedRequests.get(e.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEvent(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Такой ивент не найден"));
        if (event.getEventState() != EventState.PUBLISHED) {
            throw new ObjectNotFoundException("Событие должно быть опубликовано");
        }
        Long confirmedRequests = requestRepository.countConfirmedRequestByEventId(event.getId());
        Long views = viewService.getViewsByEvents(List.of(event)).get(id);
        viewService.sendHit(request);
        return EventMapper.toEventFullDto(event, views, confirmedRequests);
    }

}
