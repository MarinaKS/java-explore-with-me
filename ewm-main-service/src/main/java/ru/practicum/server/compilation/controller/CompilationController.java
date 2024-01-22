package ru.practicum.server.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.compilation.dto.CompilationDto;
import ru.practicum.server.compilation.dto.CompilationNewDto;
import ru.practicum.server.compilation.dto.CompilationUpdateDto;
import ru.practicum.server.compilation.service.CompilationService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                                @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        return compilationService.getCompilation(compId);
    }

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody CompilationNewDto compilationNewDto) {
        return compilationService.addCompilation(compilationNewDto);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId, @Valid @RequestBody CompilationUpdateDto compilationUpdateDto) {
        return compilationService.updateCompilation(compId, compilationUpdateDto);
    }
}
