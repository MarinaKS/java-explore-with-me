package ru.practicum.server.compilation.service;

import ru.practicum.server.compilation.dto.CompilationDto;
import ru.practicum.server.compilation.dto.CompilationNewDto;
import ru.practicum.server.compilation.dto.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilation(Long compId);

    CompilationDto addCompilation(CompilationNewDto compilationNewDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, CompilationUpdateDto compilationUpdateDto);
}
