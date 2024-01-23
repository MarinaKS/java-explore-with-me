package ru.practicum.server.compilation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
public class CompilationNewDto {
    private List<Long> events;
    private boolean pinned;
    @NotBlank
    private String title;
}
