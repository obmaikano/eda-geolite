package com.geolite.projects.event;

import lombok.Getter;

import java.util.UUID;

/**
 * Base class for all events.
 *
 * <p>Each event has a unique identifier ({@link #getEventId()}) and a timestamp ({@link #getTimestamp()})
 * representing when the event was created.
 *
 * @author Obakeng Maikano
 * @since 1.0.0
 */
@Getter
public abstract class Event {
    private final UUID eventId; // Unique identifier for the event
    private final long timestamp; // Timestamp of when the event was created

    public Event() {
        this.eventId = UUID.randomUUID(); // Generate a new UUID for each event
        this.timestamp = System.currentTimeMillis(); // Capture the current time in milliseconds
    }

    public UUID getEventId() {
        return eventId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
