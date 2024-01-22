package ru.practicum.server.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.server.request.model.RequestStatusToUpdate;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class RequestEventStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatusToUpdate status;
}
