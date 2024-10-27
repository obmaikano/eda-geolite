package com.geolite.drilling.eventstore;

import com.geolite.drilling.event.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryEventStore implements EventStore {
    private final Map<UUID, List<Event>> eventStore = new ConcurrentHashMap<>();

    @Override
    public void saveEvent(Event event) {
        UUID holeId = getHoleIdFromEvent(event);
        eventStore.computeIfAbsent(holeId, k -> new ArrayList<>()).add(event);
    }

    @Override
    public List<Event> getEventsForDrilling(UUID projectId) {
        return eventStore.getOrDefault(projectId, new ArrayList<>());
    }

    private UUID getHoleIdFromEvent(Event event) {
        if (event instanceof DrillingStartedEvent) {
            return ((DrillingStartedEvent) event).getHoleId();
        } else if (event instanceof DrillingUpdatedEvent) {
            return ((DrillingUpdatedEvent) event).getHoleId();
        } else if (event instanceof DrillingDeletedEvent) {
            return ((DrillingDeletedEvent) event).getHoleId();
        } else if (event instanceof DrillingCompletedEvent) {
            return ((DrillingCompletedEvent) event).getHoleId();
        }
        throw new IllegalArgumentException("Unknown event type");
    }
}
