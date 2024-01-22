package ru.practicum.server.compilation.mapper;

import ru.practicum.server.compilation.dto.CompilationDto;
import ru.practicum.server.compilation.model.Compilation;
import ru.practicum.server.event.dto.EventShortDto;
import ru.practicum.server.event.mapper.EventMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation, Map<Long, Long> allEventsViews, Map<Long, Long> confirmedRequestsByEventsIds) {
        List<EventShortDto> eventShortDtos = compilation.getEvents()
                .stream()
                .map(e -> EventMapper.toEventShortDto(
                                e,
                                allEventsViews.get(e.getId()),
                                confirmedRequestsByEventsIds.get(e.getId())
                        )
                )
                .collect(Collectors.toList());
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(eventShortDtos)
                .build();
    }
}
