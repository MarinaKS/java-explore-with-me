package ru.practicum.server.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class RequestEventStatusUpdateResult {
    private List<RequestParticipationDto> confirmedRequests;
    private List<RequestParticipationDto> rejectedRequests;
}
