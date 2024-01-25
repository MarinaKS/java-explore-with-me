package ru.practicum.server.request.service;

import ru.practicum.server.event.model.Event;
import ru.practicum.server.request.dto.RequestEventStatusUpdateRequest;
import ru.practicum.server.request.dto.RequestEventStatusUpdateResult;
import ru.practicum.server.request.dto.RequestParticipationDto;

import java.util.List;
import java.util.Map;

public interface RequestService {
    List<RequestParticipationDto> getUserRequests(Long userId);

    RequestParticipationDto addRequest(Long userId, Long eventId);

    RequestParticipationDto cancelRequest(Long userId, Long requestId);

    RequestEventStatusUpdateResult updateRequest(Long userId, Long eventId, RequestEventStatusUpdateRequest requestStatusUpdateDto);

    List<RequestParticipationDto> getRequestsByEventIdAndInitiatorId(Long userId, Long eventId);

    Map<Long, Long> getConfirmedRequestCountsByEventsIds(List<Event> events);
}
