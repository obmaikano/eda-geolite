package com.geolite.drilling.eventstore;

import com.geolite.drilling.event.Event;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventStore {
    void saveEvent(Event event);
    List<Event> getEventsForDrilling(UUID holeId);
}
