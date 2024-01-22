package ru.practicum.server.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.event.model.Event;
import ru.practicum.server.event.model.EventState;
import ru.practicum.server.event.repository.EventRepository;
import ru.practicum.server.exception.ConflictException;
import ru.practicum.server.exception.ObjectNotFoundException;
import ru.practicum.server.request.dto.RequestEventStatusUpdateRequest;
import ru.practicum.server.request.dto.RequestEventStatusUpdateResult;
import ru.practicum.server.request.dto.RequestParticipationDto;
import ru.practicum.server.request.mapper.RequestMapper;
import ru.practicum.server.request.model.Request;
import ru.practicum.server.request.model.RequestStatus;
import ru.practicum.server.request.model.RequestStatusToUpdate;
import ru.practicum.server.request.repository.ConfirmedRequestsByEventIdRow;
import ru.practicum.server.request.repository.RequestRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<RequestParticipationDto> getUserRequests(Long userId) {
        log.info("Request sent");
        return requestRepository.findByRequesterId(userId)
                .stream()
                .map(RequestMapper::toRequestParticipationDto)
                .collect(Collectors.toList()
                );
    }

    @Transactional
    @Override
    public RequestParticipationDto addRequest(Long userId, Long eventId) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Нельзя добавить повторный запрос");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Событие не найдено"));
        if (event.getEventState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Пользователь с таким id не найден");
        });
        int confirmedRequests = requestRepository.findByEventIdConfirmed(eventId).size();
        if (confirmedRequests >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ConflictException("У события достигнут лимит запросов на участие");
        }
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        log.info("Запрос создан");
        return RequestMapper.toRequestParticipationDto(requestRepository.save(request));
    }

    @Override
    public RequestParticipationDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрос не найден"));
        request.setStatus(RequestStatus.CANCELED);
        log.info("Request canceled");
        return RequestMapper.toRequestParticipationDto(requestRepository.save(request));
    }

    @Override
    public RequestEventStatusUpdateResult updateRequest(Long userId,
                                                        Long eventId,
                                                        RequestEventStatusUpdateRequest requestEventStatusUpdateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Событие не найдено"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new IllegalArgumentException("Рассматривать заявки может только создатель события");
        }
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Подтверждение заявки не требуется");
        }
        RequestEventStatusUpdateResult requestEventStatusUpdateResult =
                new RequestEventStatusUpdateResult(new ArrayList<>(), new ArrayList<>());
        Integer confirmedRequests = requestRepository.findByEventIdConfirmed(eventId).size();
        List<Request> requests = requestRepository.findByEventIdAndRequestsIds(eventId,
                requestEventStatusUpdateRequest.getRequestIds());
        if (confirmedRequests + requests.size() > event.getParticipantLimit() &&
                requestEventStatusUpdateRequest.getStatus() == RequestStatusToUpdate.CONFIRMED) {
            throw new ConflictException("Лимит заявок для события исчерпан");
        }
        if (requestEventStatusUpdateRequest.getStatus() == RequestStatusToUpdate.REJECTED) {
            requests.forEach(request -> {
                if (request.getStatus() == (RequestStatus.CONFIRMED)) {
                    throw new ConflictException("Нельзя отклонить одобренный запрос");
                }
                request.setStatus(RequestStatus.REJECTED);
            });
            List<RequestParticipationDto> requestParticipationDtos = requests
                    .stream()
                    .map(RequestMapper::toRequestParticipationDto)
                    .collect(Collectors.toList());
            requestEventStatusUpdateResult.setRejectedRequests(requestParticipationDtos);
            requestRepository.saveAll(requests);
        } else if (requestEventStatusUpdateRequest.getStatus() == RequestStatusToUpdate.CONFIRMED) {
            requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
            List<RequestParticipationDto> requestParticipationDtos = requests
                    .stream()
                    .map(RequestMapper::toRequestParticipationDto)
                    .collect(Collectors.toList());
            requestEventStatusUpdateResult.setConfirmedRequests(requestParticipationDtos);
            requestRepository.saveAll(requests);
        }
        return requestEventStatusUpdateResult;
    }

    @Override
    public List<RequestParticipationDto> getRequestsByEventIdAndInitiatorId(Long userId, Long eventId) {
        return requestRepository.findByEventIdAndInitiatorId(eventId, userId)
                .stream()
                .map(RequestMapper::toRequestParticipationDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Long> getConfirmedRequestCountsByEventsIds(List<Event> events) {
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<ConfirmedRequestsByEventIdRow> counts = requestRepository.countConfirmedRequestByEventIds(eventIds);
        Map<Long, Long> confirmedRequestsByEventId = counts.stream()
                .collect(Collectors.toMap(ConfirmedRequestsByEventIdRow::getEventId,
                        ConfirmedRequestsByEventIdRow::getConfirmedRequests));
        return events.stream().collect(Collectors.toMap(Event::getId,
                e -> confirmedRequestsByEventId.getOrDefault(e.getId(), 0L),
                (e1, e2) -> e2));
    }
}
