package ru.practicum.stats.dto.model;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
public class ViewStats {
    private String app;

    private String uri;

    private Long hits;
}
