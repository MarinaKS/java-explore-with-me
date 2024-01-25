package ru.practicum.server.request.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.server.request.dto.RequestParticipationDto;
import ru.practicum.server.request.model.Request;

@Service
public class RequestMapper {
    public static RequestParticipationDto toRequestParticipationDto(Request request) {
        return new RequestParticipationDto(
                request.getId(),
                request.getCreated(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus());
    }
}
