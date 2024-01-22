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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViewService {
    private final StatsClient statsClient;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Map<Long, Long> getViewsByEvents(List<Event> events) {
        LocalDateTime minStart = events
                .stream()
                .map(Event::getCreatedOn)
                .min(Comparator.naturalOrder())
                .get();
        Map<String, Long> eventUris = events
                .stream()
                .collect(Collectors.toMap(e -> "/events/" + e.getId(), e -> e.getId()));
        List<ViewStats> stats = statsClient.getStats(
                minStart.format(dateTimeFormatter),
                LocalDateTime.now().format(dateTimeFormatter),
                new ArrayList<>(eventUris.keySet()),
                false);
        return stats.stream().collect(Collectors.toMap(s -> eventUris.get(s.getUri()), s -> s.getHits()));
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
