package com.geolite.scenarios.eventstore;

import com.geolite.scenarios.event.Event;
import com.geolite.scenarios.event.ScenarioCreatedEvent;
import com.geolite.scenarios.event.ScenarioDeletedEvent;
import com.geolite.scenarios.event.ScenarioUpdatedEvent;
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
        UUID scenarioId = getScenarioIdFromEvent(event);
        eventStore.computeIfAbsent(scenarioId, k -> new ArrayList<>()).add(event);
    }

    @Override
    public List<Event> getEventsForScenario(UUID scenarioId) {
        return eventStore.getOrDefault(scenarioId, new ArrayList<>());
    }

    private UUID getScenarioIdFromEvent(Event event) {
        if (event instanceof ScenarioCreatedEvent) {
            return ((ScenarioCreatedEvent) event).getScenarioId();
        } else if (event instanceof ScenarioUpdatedEvent) {
            return ((ScenarioUpdatedEvent) event).getScenarioId();
        } else if (event instanceof ScenarioDeletedEvent) {
            return ((ScenarioDeletedEvent) event).getScenarioId();
        }
        throw new IllegalArgumentException("Unknown event type");
    }
}