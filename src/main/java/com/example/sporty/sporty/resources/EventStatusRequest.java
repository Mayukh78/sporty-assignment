package com.example.sporty.sporty.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class EventStatusRequest {
    @NotBlank(message = "Event id can not be empty")
    private String eventId;

    @NotNull(message = "Status can not be null")
    @Pattern(regexp = "LIVE|NOT_LIVE", message = "Status can only be LIVE or NOT_LIVE")
    private String status;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isLive() {
        if (EventStatus.LIVE.name().equals(status)) {
            return true;
        } else {
            return false;
        }
    }
}
