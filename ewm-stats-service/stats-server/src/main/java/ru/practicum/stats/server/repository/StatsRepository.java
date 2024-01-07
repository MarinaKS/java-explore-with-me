package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.server.model.Stats;
import ru.practicum.stats.server.model.ViewStatsRow;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {
    @Query(value = "SELECT new ru.practicum.stats.server.model.ViewStatsRow(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stats s where s.timestamp BETWEEN :start AND :end AND (s.uri IN (:uris) OR :uris = null)" +
            "GROUP BY s.app, s.uri ORDER BY COUNT(s.ip) DESC"
    )
    List<ViewStatsRow> getStatsByDate(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end,
                                      @Param("uris") List<String> uris);

    @Query(value = "SELECT new ru.practicum.stats.server.model.ViewStatsRow(s.app, s.uri, COUNT(distinct s.ip)) " +
            "FROM Stats s where s.timestamp BETWEEN :start AND :end AND (s.uri in :uris OR :uris = null) " +
            "GROUP BY s.app, s.uri ORDER BY COUNT(distinct s.ip) DESC"
    )
    List<ViewStatsRow> getStatsByDateUniqueIp(LocalDateTime start,
                                           LocalDateTime end,
                                           List<String> uris);
}
