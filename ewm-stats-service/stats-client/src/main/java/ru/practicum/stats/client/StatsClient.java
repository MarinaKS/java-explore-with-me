package ru.practicum.stats.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.dto.model.EndpointHit;
import ru.practicum.stats.dto.model.ViewStats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatsClient {
    private final String serverUrl;
    private final RestTemplate restTemplate;

    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplate restTemplate) {
        this.serverUrl = serverUrl;
        this.restTemplate = restTemplate;
    }

    public void addStats(EndpointHit endpointHit) {
        log.info("endpointHit: {}", endpointHit);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHit> requestEntity = new HttpEntity<>(endpointHit, headers);
        restTemplate.exchange(serverUrl + "/hit", HttpMethod.POST, requestEntity, EndpointHit.class);
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        log.info("start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        StringBuilder uriBuilder = new StringBuilder(serverUrl + "/stats?start={start}&end={end}");
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end);
        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                uriBuilder.append("&uris=").append(uri);
            }
        }
        if (unique != null) {
            uriBuilder.append("&unique=").append(unique);
        }
        String responseBody = restTemplate.getForEntity(
                uriBuilder.toString(),
                String.class, parameters).getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return Arrays.asList(objectMapper.readValue(responseBody, ViewStats[].class));
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Json error", exception);
        }
    }
}
