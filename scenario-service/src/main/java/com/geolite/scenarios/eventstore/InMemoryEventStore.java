package com.geolite.scenarios.eventstore;

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
        private final Map<UUID, List<Object>> eventStore = new ConcurrentHashMap<>();

    @Override
    public void saveEvent(Object event) {
        UUID scenarioId = getScenarioIdFromEvent(event);
        eventStore.computeIfAbsent(scenarioId, k -> new ArrayList<>()).add(event);
    }

    @Override
    public List<Object> getEventsForScenario(UUID scenarioId) {
        return eventStore.getOrDefault(scenarioId, new ArrayList<>());
    }

    private UUID getScenarioIdFromEvent(Object event) {
        if (event instanceof ScenarioCreatedEvent) {
            String scenarioId = ((ScenarioCreatedEvent) event).getScenarioId().toString();
            return UUID.fromString(scenarioId);
        } else if (event instanceof ScenarioUpdatedEvent) {
            String scenarioId = ((ScenarioUpdatedEvent) event).getScenarioId().toString();
            return UUID.fromString(scenarioId);
        } else if (event instanceof ScenarioDeletedEvent) {
            String scenarioId = ((ScenarioDeletedEvent) event).getScenarioId().toString();
            return UUID.fromString(scenarioId);
        }
        throw new IllegalArgumentException("Unknown event type");
    }
}