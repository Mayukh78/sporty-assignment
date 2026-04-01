package com.example.sporty.sporty.controller;

import com.example.sporty.sporty.resources.EventStatusRequest;
import com.example.sporty.sporty.resources.EventStatusResponse;
import com.example.sporty.sporty.service.EventTrackerService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events/")
public class EventController {

    private final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventTrackerService eventTrackerService;

    /**
     * Updates the status of a sports event.
     * If status is true (live), periodic polling starts..
     * * @param request JSON payload containing eventId and isLive status
     * @return HTTP 200 OK with a confirmation message
     */
    @PostMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventStatusResponse> updateEventStatus(
            @Valid @RequestBody EventStatusRequest request) {
        logger.info("Received status update request for Event ID: {} with Status: {}",
                request.getEventId(), request.getStatus());


        return ResponseEntity.ok(eventTrackerService.updateEventStatus(request));
    }
}