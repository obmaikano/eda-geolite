package com.geolite.projects.eventstore;

import com.geolite.projects.event.ProjectCreatedEvent;
import com.geolite.projects.event.ProjectDeletedEvent;
import com.geolite.projects.event.ProjectUpdatedEvent;
import com.geolite.projects.util.ConversionUtil;

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
        UUID projectId = getProjectIdFromEvent(event);
        eventStore.computeIfAbsent(projectId, k -> new ArrayList<>()).add(event);
    }

    @Override
    public List<Object> getEventsForProject(UUID projectId) {
        return eventStore.getOrDefault(projectId, new ArrayList<>());
    }

    private UUID getProjectIdFromEvent(Object event) {
        if (event instanceof ProjectCreatedEvent) {
            String projectId = ((ProjectCreatedEvent) event).getProjectId().toString();
            return ConversionUtil.stringToUUID(projectId);
        } else if (event instanceof ProjectUpdatedEvent) {
            String projectId = ((ProjectUpdatedEvent) event).getProjectId().toString();
            return ConversionUtil.stringToUUID(projectId);
        } else if (event instanceof ProjectDeletedEvent) {
            String projectId = ((ProjectUpdatedEvent) event).getProjectId().toString();
            return ConversionUtil.stringToUUID(projectId);
        }
        throw new IllegalArgumentException("Unknown event type");
    }
}
