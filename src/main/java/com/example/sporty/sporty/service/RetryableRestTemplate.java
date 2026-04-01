package com.example.sporty.sporty.service;

import com.example.sporty.sporty.resources.ExternalEventData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

@Component
public class RetryableRestTemplate {
    private final Logger logger = LoggerFactory.getLogger(RetryableRestTemplate.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RestTemplate restTemplate;

    @Value("${external.api.url}")
    private String externalApiUrl;

    @Retryable(
            retryFor = { ResourceAccessException.class, HttpServerErrorException.class, HttpClientErrorException.TooManyRequests.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public ExternalEventData getFromExternalApi(String eventId) {
        logger.info("Calling external api for event Id {}", eventId);
        ExternalEventData eventData = restTemplate.getForObject(buildGetByIdUrl(externalApiUrl ,eventId), ExternalEventData.class);
        logger.info("Received {} from external api", objectMapper.writeValueAsString(eventData));
        return eventData;
    }

    public String buildGetByIdUrl(String url, String eventId) {
        return UriComponentsBuilder.fromUriString(url)
                .path("{eventId}")
                .buildAndExpand(eventId)
                .toUriString();
    }
}
