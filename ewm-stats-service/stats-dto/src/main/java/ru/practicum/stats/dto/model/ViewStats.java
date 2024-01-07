package ru.practicum.stats.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ViewStats {
    private String app;

    private String uri;

    private Long hits;
}
