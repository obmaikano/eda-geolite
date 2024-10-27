package com.geolite.projects.service;

import com.geolite.projects.event.ProjectCreatedEvent;
import com.geolite.projects.event.ProjectDeletedEvent;
import com.geolite.projects.event.ProjectUpdatedEvent;
import com.geolite.projects.model.Project;
import com.geolite.projects.repository.ProjectRepository;
import com.geolite.projects.util.ConversionUtil;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsistencyService {

    @Autowired
    private ProjectRepository projectRepository;

    @KafkaListener(topics = {"project-created", "project-updated", "project-deleted"}, groupId = "consistency-group")
    public void ensureConsistency(ConsumerRecord<String, Object> record) {
        String topic = record.topic();
        Object value = record.value();

        switch (topic) {
            case "project-created":
                handleProjectCreated((ProjectCreatedEvent) value);
                break;
            case "project-updated":
                handleProjectUpdated((ProjectUpdatedEvent) value);
                break;
            case "project-deleted":
                handleProjectDeleted((ProjectDeletedEvent) value);
                break;
            default:
                throw new IllegalArgumentException("Unknown topic: " + topic);
        }
    }

    private void handleProjectCreated(ProjectCreatedEvent event) {
        projectRepository
                .findById(ConversionUtil.stringToUUID(event.getProjectId().toString())).ifPresent(project -> {
                    updateProjectFromEvent(project, event);
                    project.setProjectId(ConversionUtil.stringToUUID(event.getProjectId().toString()));
                    projectRepository.save(project);
                });
    }

    private void handleProjectUpdated(ProjectUpdatedEvent event) {
        projectRepository.findById(ConversionUtil.stringToUUID(event.getProjectId().toString()))
                .ifPresent(project -> {
                    updateProjectFromEvent(project, event);
                    projectRepository.save(project);
                });
    }

    private void handleProjectDeleted(ProjectDeletedEvent event) {
        projectRepository.deleteById(ConversionUtil.stringToUUID(event.getProjectId().toString()));
    }

    private void updateProjectFromEvent(Project project, ProjectCreatedEvent event) {
        project.setProjectId(ConversionUtil.stringToUUID(event.getProjectId().toString()));
        project.setProjectName(event.getProjectName().toString());
        project.setDescription(event.getDescription().toString());
        project.setCreatedBy(event.getCreatedBy().toString());
        project.setCreatedDate(ConversionUtil.longToTimestamp(event.getCreatedDate()));
        project.setLocation(event.getLocation().toString());
        project.setCommodity(event.getCommodity().toString());
        project.setStartDate(ConversionUtil.longToDate(event.getStartDate()));
        project.setEndDate(ConversionUtil.longToDate(event.getEndDate()));
        project.setProjectLead(event.getProjectLead().toString());
    }

    private void updateProjectFromEvent(Project project, ProjectUpdatedEvent event) {
        // Update only the fields that are present in the UpdatedEvent
        if (event.getProjectName() != null) project.setProjectName(event.getProjectName().toString());
        if (event.getDescription() != null) project.setDescription(event.getDescription().toString());
        if (event.getModifiedBy() != null) project.setModifiedBy(event.getModifiedBy().toString());
        if (ConversionUtil.longToTimestamp(event.getModifiedDate()) != null) project.setModifiedDate(ConversionUtil.longToTimestamp(event.getModifiedDate()));
        if (event.getLocation() != null) project.setLocation(event.getLocation().toString());
        if (event.getCommodity() != null) project.setCommodity(event.getCommodity().toString());
        if (ConversionUtil.longToDate(event.getStartDate()) != null) project.setStartDate(ConversionUtil.longToDate(event.getStartDate()));
        if (ConversionUtil.longToDate(event.getEndDate()) != null) project.setEndDate(ConversionUtil.longToDate(event.getEndDate()));
        if (event.getProjectLead() != null) project.setProjectLead(event.getProjectLead().toString());
    }
}
