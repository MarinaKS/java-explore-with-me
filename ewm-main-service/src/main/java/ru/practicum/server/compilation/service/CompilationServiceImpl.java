package ru.practicum.server.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.server.compilation.dto.CompilationDto;
import ru.practicum.server.compilation.dto.CompilationNewDto;
import ru.practicum.server.compilation.dto.CompilationUpdateDto;
import ru.practicum.server.compilation.mapper.CompilationMapper;
import ru.practicum.server.compilation.model.Compilation;
import ru.practicum.server.compilation.repository.CompilationRepository;
import ru.practicum.server.event.model.Event;
import ru.practicum.server.event.repository.EventRepository;
import ru.practicum.server.exception.ObjectNotFoundException;
import ru.practicum.server.request.service.RequestService;
import ru.practicum.server.view.service.ViewService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final ViewService viewService;
    private final RequestService requestService;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, page);
        List<Event> allEvents = compilations.stream().flatMap(c -> c.getEvents().stream()).collect(Collectors.toList());
        Map<Long, Long> allEventsViews = viewService.getViewsByEvents(allEvents);
        Map<Long, Long> confirmedRequestsByEventsIds = requestService.getConfirmedRequestCountsByEventsIds(allEvents);
        return compilations.stream()
                .map(c -> CompilationMapper.toCompilationDto(c, allEventsViews, confirmedRequestsByEventsIds))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Подборка с таким id не найдена"));
        return CompilationMapper.toCompilationDto(
                compilation,
                viewService.getViewsByEvents(compilation.getEvents()),
                requestService.getConfirmedRequestCountsByEventsIds(compilation.getEvents()));
    }

    @Override
    public CompilationDto addCompilation(CompilationNewDto compilationNewDto) {
        List<Event> events = eventRepository.findAllByIdIn(compilationNewDto.getEvents());
        Compilation compilation = new Compilation();
        compilation.setEvents(events);
        compilation.setPinned(compilationNewDto.getPinned());
        compilation.setTitle(compilationNewDto.getTitle());
        Compilation savedCompilation = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(
                savedCompilation,
                viewService.getViewsByEvents(savedCompilation.getEvents()),
                requestService.getConfirmedRequestCountsByEventsIds(savedCompilation.getEvents()));
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
        log.info("Подборка была удалена");
    }

    @Override
    public CompilationDto updateCompilation(Long compId, CompilationUpdateDto compilationUpdateDto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Подборка не найдена"));
        List<Long> eventsIds = compilationUpdateDto.getEvents();
        if (eventsIds != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(compilationUpdateDto.getEvents()));
        }
        if (compilationUpdateDto.getPinned() != null) {
            compilation.setPinned(compilationUpdateDto.getPinned());
        }
        if (compilationUpdateDto.getTitle() != null) {
            compilation.setTitle(compilationUpdateDto.getTitle());
        }
        Compilation updateCompilation = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(
                updateCompilation,
                viewService.getViewsByEvents(compilation.getEvents()),
                requestService.getConfirmedRequestCountsByEventsIds(compilation.getEvents()));
    }
}
