package ru.practicum.server.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.server.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long userId);

    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    @Query("select r from Request r " +
            "where r.event.id = :eventId and r.status = ru.practicum.server.request.model.RequestStatus.CONFIRMED")
    List<Request> findByEventIdConfirmed(Long eventId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    @Query("select r from Request r " +
            "where r.event.id = :eventId " +
            "and r.id IN (:requestIds)")
    List<Request> findByEventIdAndRequestsIds(Long eventId, List<Long> requestIds);

    @Query("select r from Request r " +
            "where r.event.id = :eventId " +
            "and r.event.initiator.id = :userId")
    List<Request> findByEventIdAndInitiatorId(Long eventId, Long userId);

    Long countConfirmedRequestByEventId(Long eventId);

    @Query("SELECT new ru.practicum.server.request.repository.ConfirmedRequestsByEventIdRow(r.event.id, COUNT(r.id)) " +
            "FROM Request r where r.event.id in (:eventIds) and r.status = ru.practicum.server.request.model.RequestStatus.CONFIRMED " +
            "GROUP BY r.event.id")
    List<ConfirmedRequestsByEventIdRow> countConfirmedRequestByEventIds(List<Long> eventIds);

}
