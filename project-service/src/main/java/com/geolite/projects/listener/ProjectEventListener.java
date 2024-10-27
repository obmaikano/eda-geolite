package com.geolite.projects.listener;

import com.geolite.projects.event.ProjectCreatedEvent;
import com.geolite.projects.event.ProjectDeletedEvent;
import com.geolite.projects.event.ProjectUpdatedEvent;
import com.geolite.projects.eventstore.EventStore;
import com.geolite.projects.model.Project;
import com.geolite.projects.repository.ProjectRepository;
import com.geolite.projects.service.EventProcessingTracker;
import com.geolite.projects.util.ConversionUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class ProjectEventListener {

    @Autowired
    private EventStore eventStore;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EventProcessingTracker eventProcessingTracker;

    @KafkaListener(topics = "project-created", groupId = "project-group")
    @Retryable(maxAttempts = 3, value = Exception.class)
    @CircuitBreaker(name = "projectCreated", fallbackMethod = "fallbackProjectCreated")
    public void handleProjectCreated(ProjectCreatedEvent event) {
        log.info("Received ProjectCreatedEvent: {}", event);
        if (eventProcessingTracker.isEventProcessed(ConversionUtil.stringToUUID(event.getEventId().toString()))) {
            log.info("Event {} already processed, skipping", event.getEventId());
            return; // Event already processed, ensure idempotency
        }
        eventStore.saveEvent(event);
        Project project = new Project();
        project.setProjectId(ConversionUtil.stringToUUID(event.getProjectId().toString()));
        applyEvent(project, event);
        projectRepository.save(project);
        eventProcessingTracker.markEventAsProcessed(ConversionUtil.stringToUUID(event.getEventId().toString()));
    }

    @KafkaListener(topics = "project-updated", groupId = "project-group")
    @Retryable(maxAttempts = 3, value = Exception.class)
    @CircuitBreaker(name = "projectUpdated", fallbackMethod = "fallbackProjectUpdated")
    public void handleProjectUpdated(ProjectUpdatedEvent event) {
        log.info("Received ProjectUpdatedEvent: {}", event);
        if (eventProcessingTracker.isEventProcessed(ConversionUtil.stringToUUID(event.getEventId().toString()))) {
            return; // Event already processed, ensure idempotency
        }
        eventStore.saveEvent(event);
        Project project = projectRepository.findById(ConversionUtil.stringToUUID(event.getProjectId().toString()))
                .orElseThrow(() -> new RuntimeException("Project not found"));
        applyEvent(project, event);
        projectRepository.save(project);
        eventProcessingTracker.markEventAsProcessed(ConversionUtil.stringToUUID(event.getEventId().toString()));
    }

    public void handleProjectDeleted(ProjectDeletedEvent event) {
        if (eventProcessingTracker.isEventProcessed(ConversionUtil.stringToUUID(event.getEventId().toString()))) {
            return; // Event already processed, ensure idempotency
        }
        eventStore.saveEvent(event);
        projectRepository.deleteById(ConversionUtil.stringToUUID(event.getProjectId().toString()));
        eventProcessingTracker.markEventAsProcessed(ConversionUtil.stringToUUID(event.getEventId().toString()));
    }


    public Project reconstructProject(UUID projectId) {
        List<Object> events = eventStore.getEventsForProject(projectId);
        Project project = new Project();
        for (Object event : events) {
            applyEvent(project, event);
        }
        return project;
    }

    private void applyEvent(Project project, Object event) {
        if (event instanceof ProjectCreatedEvent) {
            String projectId = ((ProjectCreatedEvent) event).getProjectId().toString();
            long createDate = ((ProjectCreatedEvent) event).getCreatedDate();
            long startDate = ((ProjectCreatedEvent) event).getStartDate();
            long endDate = ((ProjectCreatedEvent) event).getEndDate();

            project.setProjectId(ConversionUtil.stringToUUID(projectId));
            project.setProjectName(((ProjectCreatedEvent) event).getProjectName().toString());
            project.setDescription(((ProjectCreatedEvent) event).getDescription().toString());
            project.setCreatedBy(((ProjectCreatedEvent) event).getCreatedBy().toString());
            project.setCreatedDate(ConversionUtil.longToTimestamp(createDate));
            project.setLocation(((ProjectCreatedEvent) event).getLocation().toString());
            project.setCommodity(((ProjectCreatedEvent) event).getCommodity().toString());
            project.setStartDate(ConversionUtil.longToDate(startDate));
            project.setEndDate(ConversionUtil.longToDate(endDate));
            project.setProjectLead(((ProjectCreatedEvent) event).getProjectLead().toString());
        } else if (event instanceof ProjectUpdatedEvent) {
            String projectId = ((ProjectUpdatedEvent) event).getProjectId().toString();
            long modifiedDate = ((ProjectUpdatedEvent) event).getModifiedDate();
            long startDate = ((ProjectUpdatedEvent) event).getStartDate();
            long endDate = ((ProjectUpdatedEvent) event).getEndDate();

            project.setProjectId(ConversionUtil.stringToUUID(projectId));
            project.setProjectName(((ProjectUpdatedEvent) event).getProjectName().toString());
            project.setDescription(((ProjectUpdatedEvent) event).getDescription().toString());
            project.setModifiedBy(((ProjectUpdatedEvent) event).getModifiedBy().toString());
            project.setModifiedDate(ConversionUtil.longToTimestamp(modifiedDate));
            project.setLocation(((ProjectUpdatedEvent) event).getLocation().toString());
            project.setCommodity(((ProjectUpdatedEvent) event).getCommodity().toString());
            project.setStartDate(ConversionUtil.longToDate(startDate));
            project.setEndDate(ConversionUtil.longToDate(endDate));
            project.setProjectLead(((ProjectUpdatedEvent) event).getProjectLead().toString());
        }
    }
}
