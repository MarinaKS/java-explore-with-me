package ru.practicum.server.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.server.event.dto.EventShortDto;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CompilationDto {
    private Long id;
    private boolean pinned;
    @Size(max = 50)
    private String title;
    private List<EventShortDto> events;
}
