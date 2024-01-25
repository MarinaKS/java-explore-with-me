package ru.practicum.server.event.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.server.event.model.Event;
import ru.practicum.server.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Boolean existsByCategoryId(Long catId);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    @Query("select e from Event e " +
            "where (:query is null or lower(e.annotation) like lower(concat('%', :query, '%')) or lower(e.description) like lower(concat('%', :query, '%'))) " +
            "and ((:states) is null or e.eventState in (:states)) " +
            "and ((:categories) is null or e.category.id in (:categories)) " +
            "and (:paid is null or e.paid = :paid) " +
            "and (CAST(:rangeStart AS timestamp) is null or e.eventDate >= :rangeStart) " +
            "and (CAST(:rangeEnd AS timestamp) is null or e.eventDate <= :rangeEnd) " +
            "and (:onlyAvailable = false or " +
            "(select count(r) from Request r where r.event.id = e.id and r.status = ru.practicum.server.request.model.RequestStatus.CONFIRMED) < e.participantLimit " +
            "or e.participantLimit = 0) " +
            "order by e.eventDate desc")
    List<Event> findByParamsOrderByDate(String query,
                                        List<EventState> states,
                                        List<Long> categories,
                                        Boolean paid,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        boolean onlyAvailable,
                                        PageRequest pageable);

    @Query("select e from Event e " +
            "where ((:userIds) is null or e.initiator.id in (:userIds)) " +
            "and ((:states) is null or e.eventState in (:states)) " +
            "and ((:categoryIds) is null or e.category.id in (:categoryIds)) " +
            "and (CAST(:rangeStart as timestamp) is null or e.eventDate >= :rangeStart) " +
            "and (CAST(:rangeEnd as timestamp) is null or e.eventDate <= :rangeEnd) " +
            "order by e.eventDate desc")
    List<Event> findEventsByAdmin(List<Long> userIds,
                                  List<EventState> states,
                                  List<Long> categoryIds,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Pageable page);

    List<Event> findAllByIdIn(List<Long> events);
}
