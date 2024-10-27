package com.geolite.scenarios.event;

import lombok.Getter;

import java.time.Instant;
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
    private final UUID eventId;
    private final Instant timestamp;

    /**
     * Creates a new event.
     *
     * <p>This constructor will assign a unique identifier ({@link #getEventId()}) and a timestamp ({@link
     * #getTimestamp()}) to the event.
     */
    public Event() {
        this.eventId = UUID.randomUUID();
        this.timestamp = Instant.now();
    }
}
