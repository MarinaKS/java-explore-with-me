package ru.practicum.stats.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewStatsRow {
    private String app;
    private String uri;
    private Long hits;
}