package ru.practicum.server.compilation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CompilationUpdateDto {
    private List<Long> events = new ArrayList<>();
    private Boolean pinned;
    @NotBlank
    @Size(max = 50)
    private String title;
}
