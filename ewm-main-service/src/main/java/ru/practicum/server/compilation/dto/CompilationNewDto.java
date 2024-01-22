package ru.practicum.server.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CompilationNewDto {
    private List<Long> events;
    private Boolean pinned;
    @NotBlank
    private String title;
}
