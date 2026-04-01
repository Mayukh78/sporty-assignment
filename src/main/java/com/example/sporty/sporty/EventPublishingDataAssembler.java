package com.example.sporty.sporty;

import com.example.sporty.sporty.resources.EventPublishData;
import com.example.sporty.sporty.resources.ExternalEventData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Component
public class EventPublishingDataAssembler {
    private final Logger logger = LoggerFactory.getLogger(EventPublishingDataAssembler.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    public String assembleEventData(ExternalEventData externalEventData) {
        EventPublishData data = constructPublishingData(externalEventData);
        String jsonData = null;
        try {
            jsonData = objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            logger.error("Encountered error while converting Event publish data to String", e);
        }
        return jsonData;

    }

    private EventPublishData constructPublishingData(ExternalEventData externalEventData) {
        EventPublishData publishingData = new EventPublishData();
        publishingData.setEventId(externalEventData.getEventId());
        publishingData.setCurrentScore(externalEventData.getCurrentScore());
        publishingData.setLastModified(Instant.now());

        return publishingData;
    }
}
