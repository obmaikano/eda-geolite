package com.geolite.scenarios.eventstore;

import com.geolite.scenarios.event.Event;

import java.util.List;
import java.util.UUID;

public interface EventStore {
    void saveEvent(Event event);
    List<Event> getEventsForScenario(UUID scenarioId);
}
