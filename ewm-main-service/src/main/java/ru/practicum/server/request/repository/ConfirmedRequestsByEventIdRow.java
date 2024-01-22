package ru.practicum.server.request.repository;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfirmedRequestsByEventIdRow {
    private Long eventId;
    private Long confirmedRequests;
}