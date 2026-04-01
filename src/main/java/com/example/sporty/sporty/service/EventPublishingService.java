package com.example.sporty.sporty.service;

import com.example.sporty.sporty.EventPublishingDataAssembler;
import com.example.sporty.sporty.resources.ExternalEventData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventPublishingService {
    private final Logger logger = LoggerFactory.getLogger(EventPublishingService.class);

    @Autowired
    private RetryableRestTemplate retryableRestTemplate;

    @Autowired
    private EventPublishingDataAssembler eventPublishingDataAssembler;

    @Autowired
    private KafkaEventDataProducer kafkaEventDataProducer;

    public void fetchAndPublish(String eventId) {
        ExternalEventData eventData = null;
        try {
            eventData = retryableRestTemplate.getFromExternalApi(eventId);
        } catch (Exception ex) {
            logger.error("Encountered exception while calling external api, not proceeding further", ex);
            return;
        }

        if (eventData != null) {
            String message = eventPublishingDataAssembler.assembleEventData(eventData);
            if (message != null) {
                kafkaEventDataProducer.sendMessage(eventId, message);
            } else {
                logger.info("As we ecountered while assembling message, we're not going to publish to kafka");
            }
        }
    }
}