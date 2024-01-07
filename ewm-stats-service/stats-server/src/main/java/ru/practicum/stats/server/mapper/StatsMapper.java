package ru.practicum.stats.server.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.model.EndpointHit;
import ru.practicum.stats.dto.model.ViewStats;
import ru.practicum.stats.server.model.Stats;
import ru.practicum.stats.server.model.ViewStatsRow;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class StatsMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static Stats toStats(EndpointHit endpointHit){
        Stats stats = new Stats(
                null,
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                LocalDateTime.parse(endpointHit.getTimestamp(), FORMATTER));
        return stats;
    }

    public static ViewStats toViewStats(ViewStatsRow viewStatsRow){
        ViewStats viewStats = new ViewStats(
                viewStatsRow.getApp(),
                viewStatsRow.getUri(),
                viewStatsRow.getHits()
        );
        return viewStats;
    }
}
