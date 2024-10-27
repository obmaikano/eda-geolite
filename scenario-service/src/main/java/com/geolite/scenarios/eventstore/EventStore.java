package com.geolite.scenarios.eventstore;

import java.util.List;
import java.util.UUID;

public interface EventStore {
    void saveEvent(Object event);
    List<Object> getEventsForScenario(UUID scenarioId);
}
