package com.geolite.projects.eventstore;

//import com.geolite.projects.event.Event;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventStore {
    void saveEvent(Object event);
    List<Object> getEventsForProject(UUID projectId);
}
