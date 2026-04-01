package com.example.sporty.sporty.service;

import com.example.sporty.sporty.EventPublishingDataAssembler;
import com.example.sporty.sporty.resources.EventStatus;
import com.example.sporty.sporty.resources.EventStatusRequest;
import com.example.sporty.sporty.resources.EventStatusResponse;
import com.example.sporty.sporty.resources.ExternalEventData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventTrackerService {

    private final Logger logger = LoggerFactory.getLogger(EventTrackerService.class);

    // Thread-safe set of IDs currently in "live" status
    private final Set<String> liveEventIds = ConcurrentHashMap.newKeySet();

    @Autowired
    private RetryableRestTemplate retryableRestTemplate;

    @Autowired
    private EventPublishingDataAssembler eventPublishingDataAssembler;

    @Autowired
    private EventPublishingService eventPublishingService;

    @Autowired
    private KafkaEventDataProducer kafkaEventDataProducer;

    public EventStatusResponse updateEventStatus(EventStatusRequest request) {
        EventStatusResponse response = new EventStatusResponse();
        response.setEventId(request.getEventId());
        if (request.isLive()) {
            liveEventIds.add(request.getEventId());
            logger.info("Event {} added to live tracking.", request.getEventId());
            response.setStatus(EventStatus.LIVE);
        } else {
            liveEventIds.remove(request.getEventId());
            logger.info("Event {} removed from live tracking.", request.getEventId());
            response.setStatus(EventStatus.NOT_LIVE);
        }
        return response;
    }

    /**
     * The Ticker: Runs every 10s.
     * It simply triggers async tasks for all live events.
     */
    @Scheduled(fixedRate = 10000)
    public void scheduleLiveUpdates() {
        if (liveEventIds.isEmpty()) return;

        logger.info("Triggering updates for {} live events", liveEventIds.size());
        for(String liveId: liveEventIds) {
            eventPublishingService.fetchAndPublish(liveId);
        }
    }


}
