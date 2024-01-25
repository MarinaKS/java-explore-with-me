package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.model.EndpointHit;
import ru.practicum.stats.dto.model.ViewStats;
import ru.practicum.stats.server.exception.ValidationException;
import ru.practicum.stats.server.mapper.StatsMapper;
import ru.practicum.stats.server.model.Stats;
import ru.practicum.stats.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsRepository statsRepository;

    @Override
    public void addHit(EndpointHit endpointHit) {
        log.info("endpoint hit {}", endpointHit);
        Stats stats = StatsMapper.toStats(endpointHit);
        statsRepository.save(stats);
    }

    @Override
    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startParsed = LocalDateTime.parse(start, FORMATTER);
        LocalDateTime endParsed = LocalDateTime.parse(end, FORMATTER);
        if (startParsed.isAfter(endParsed)) {
            throw new ValidationException("start cannot be after end");
        }
        log.info("viewStats: start = {}, end = {}, uris = {}, unique = {}",
                start, end, uris, unique);
        if (unique) {
            return statsRepository.getStatsByDateUniqueIp(startParsed, endParsed, uris)
                    .stream()
                    .map(StatsMapper::toViewStats)
                    .collect(Collectors.toList());
        } else {
            return statsRepository.getStatsByDate(startParsed, endParsed, uris)
                    .stream()
                    .map(StatsMapper::toViewStats)
                    .collect(Collectors.toList());
        }
    }
}
