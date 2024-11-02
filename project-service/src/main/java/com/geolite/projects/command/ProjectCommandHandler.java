package com.geolite.projects.command;

import com.geolite.projects.event.ProjectCreatedEvent;
import com.geolite.projects.event.ProjectDeletedEvent;
import com.geolite.projects.event.ProjectUpdatedEvent;
import com.geolite.projects.model.Project;
import com.geolite.projects.model.Status;
import com.geolite.projects.eventstore.EventStore;
import com.geolite.projects.repository.ProjectRepository;
import com.geolite.projects.util.ConversionUtil;
import com.geolite.projects.util.ProjectValidator;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class ProjectCommandHandler {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EventStore eventStore;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ProjectValidator projectValidator;

    @Transactional
    public Project handleCreateProject(CreateProjectCommand command) {

        Project project = new Project();
        // Set project properties from command
        project.setProjectName(command.getProjectName());
        project.setLocation(command.getLocation());
        project.setCommodity(command.getCommodity());
        project.setStartDate(command.getStartDate());
        project.setEndDate(command.getEndDate());
        project.setDescription(command.getDescription());
        project.setProjectLead(command.getProjectLead());

        // Validate the project
        projectValidator.validateNewProject(project);

        UUID projectId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        ProjectCreatedEvent event = new ProjectCreatedEvent(
                eventId.toString(),
                Timestamp.from(Instant.now()).getTime(),
                projectId.toString(),
                command.getProjectName(),
                command.getLocation(),
                command.getCommodity(),
                command.getStartDate().getTime(),
                command.getEndDate().getTime(),
                command.getDescription(),
                command.getProjectLead(),
                command.getCreatedBy(),
                Timestamp.from(Instant.now()).getTime()
        );

        eventStore.saveEvent(event);
        publishEvent("project-created",event);

        applyEvent(project, event);
        
        log.info("Project created : {}", command.toString());
        return projectRepository.save(project);
    }

    @Transactional
    public Project handleUpdateProject(UpdateProjectCommand command) {
        Project project = projectRepository.findById(command.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Objects.requireNonNull(command.getProjectName(), "Project name is required");
        Objects.requireNonNull(command.getLocation(), "Location is required");
        Objects.requireNonNull(command.getStartDate(), "Start date is required");
        Objects.requireNonNull(command.getProjectLead(), "Project lead is required");
        Objects.requireNonNull(command.getDescription(), "Description is required");
        Objects.requireNonNull(command.getModifiedBy(), "Modified by is required");

        UUID eventId = UUID.randomUUID();
        ProjectUpdatedEvent event = new ProjectUpdatedEvent(
                eventId.toString(),
                Timestamp.from(Instant.now()).getTime(),
                command.getProjectId().toString(),
                command.getProjectName(),
                command.getLocation(),
                command.getCommodity(),
                command.getStartDate().getTime(),
                command.getEndDate().getTime(),
                command.getDescription(),
                command.getProjectLead(),
                command.getStatus().toString(),
                command.getModifiedBy(),
                Timestamp.from(Instant.now()).getTime()
        );

        eventStore.saveEvent(event);
        publishEvent("project-updated",event);
        applyEvent(project, event);
        log.info("Project updated: {}", command.toString());
        return projectRepository.save(project);
    }

    @Transactional
    public void handleDeleteProject(DeleteProjectCommand command) {
        Project project = projectRepository.findById(command.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        UUID eventId = UUID.randomUUID();
        ProjectDeletedEvent event = new ProjectDeletedEvent(
            eventId.toString(),
            Timestamp.from(Instant.now()).getTime(),
            project.getProjectId().toString()
        );

        eventStore.saveEvent(event);
        publishEvent("project-deleted",event);
        projectRepository.deleteById(command.getProjectId());
    }

    private void publishEvent(String topic, Object event) {
         log.info("Data being sent to Kafka: {}", event.toString());
         kafkaTemplate.send(topic, event);
    }

    private void applyEvent(Project project, Object event) {
        if (event instanceof ProjectCreatedEvent createdEvent) {
            project.setProjectId(ConversionUtil.stringToUUID(createdEvent.getProjectId().toString()));
            project.setProjectName(createdEvent.getProjectName().toString());
            project.setLocation(createdEvent.getLocation().toString());
            project.setCommodity(createdEvent.getCommodity().toString());
            project.setStartDate(ConversionUtil.longToDate(createdEvent.getStartDate()));
            project.setEndDate(ConversionUtil.longToDate(createdEvent.getEndDate()));
            project.setDescription(createdEvent.getDescription().toString());
            project.setProjectLead(createdEvent.getProjectLead().toString());
            project.setStatus(Status.DRAFT);
            project.setCreatedBy(createdEvent.getCreatedBy().toString());
            project.setCreatedDate(ConversionUtil.longToTimestamp(createdEvent.getCreatedDate()));
        } else if (event instanceof ProjectUpdatedEvent updatedEvent) {
            project.setProjectId(ConversionUtil.stringToUUID(updatedEvent.getProjectId().toString()));
            project.setProjectName(updatedEvent.getProjectName().toString());
            project.setLocation(updatedEvent.getLocation().toString());
            project.setCommodity(updatedEvent.getCommodity().toString());
            project.setStartDate(ConversionUtil.longToDate(updatedEvent.getStartDate()));
            project.setEndDate(ConversionUtil.longToDate(updatedEvent.getEndDate()));
            project.setDescription(updatedEvent.getDescription().toString());
            project.setProjectLead(updatedEvent.getProjectLead().toString());
            project.setStatus(Status.fromString(updatedEvent.getStatus().toString()));
            project.setModifiedBy(updatedEvent.getModifiedBy().toString());
            project.setModifiedDate(ConversionUtil.longToTimestamp(updatedEvent.getModifiedDate()));
        }
    }
}
