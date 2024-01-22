package ru.practicum.server.view.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.server.event.model.Event;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.model.EndpointHit;
import ru.practicum.stats.dto.model.ViewStats;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViewService {
    private final StatsClient statsClient;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Map<Long, Long> getViewsByEvents(List<Event> events) {
        if (events.isEmpty()) {
            return new HashMap<>();
        }
        LocalDateTime minStart = events
                .stream()
                .map(Event::getCreatedOn)
                .min(Comparator.naturalOrder())
                .get();
        Map<String, Long> eventUris = events
                .stream()
                .collect(Collectors.toMap(e -> "/events/" + e.getId(), e -> e.getId(), (e1, e2) -> e2));
        List<ViewStats> stats = statsClient.getStats(
                minStart.format(dateTimeFormatter),
                LocalDateTime.now().format(dateTimeFormatter),
                new ArrayList<>(eventUris.keySet()),
                false);
        Map<String, Long> viewsByUris = stats.stream().collect(Collectors.toMap(ViewStats::getUri, ViewStats::getHits));
        return events.stream()
                .collect(Collectors.toMap(Event::getId,
                        e -> viewsByUris.getOrDefault("/events/" + e.getId(), 0L),
                        (e1, e2) -> e2));
    }

    public void sendHit(HttpServletRequest request) {
        String nameService = "ewm-main-service";
        String remoteAddr = request.getRemoteAddr();
        statsClient.addStats(new EndpointHit(
                nameService,
                request.getRequestURI(),
                remoteAddr,
                LocalDateTime.now().format(dateTimeFormatter)));
    }
}
