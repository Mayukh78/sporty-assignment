package com.example.sporty.sporty.resources;

public class EventStatusResponse {
    private String eventId;
    private EventStatus status;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
}
