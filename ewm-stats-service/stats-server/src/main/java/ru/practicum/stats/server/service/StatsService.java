package ru.practicum.stats.server.service;

import ru.practicum.stats.dto.model.EndpointHit;
import ru.practicum.stats.dto.model.ViewStats;

import java.util.List;

public interface StatsService {
    void addHit(EndpointHit endpointHit);

    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);
}
