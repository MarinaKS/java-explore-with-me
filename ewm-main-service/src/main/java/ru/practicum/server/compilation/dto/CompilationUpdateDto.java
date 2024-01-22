package ru.practicum.server.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CompilationUpdateDto {
    private List<Long> events;
    private Boolean pinned;
    private String title;
}
