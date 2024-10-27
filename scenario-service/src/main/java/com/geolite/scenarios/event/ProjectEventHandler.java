package com.geolite.scenarios.event;

import com.geolite.scenarios.model.ProjectReadModel;
import com.geolite.scenarios.repository.ProjectReadModelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProjectEventHandler {

    @Autowired
    private ProjectReadModelRepository projectReadModelRepository;

    @KafkaListener(topics = "project-created", groupId = "scenario-group")
    public void handleProjectCreated(ProjectCreatedEvent event) {
        ProjectReadModel readModel = new ProjectReadModel();
        updateReadModelFromEvent(readModel, event);
        log.info("Project created by: {}", event.getCreatedBy());
        projectReadModelRepository.save(readModel);
    }

    @KafkaListener(topics = "project-updated", groupId = "scenario-group")
    public void handleProjectUpdated(ProjectUpdatedEvent event) {
        ProjectReadModel readModel = projectReadModelRepository.findById(event.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found in read model"));
        updateReadModelFromEvent(readModel, event);
        projectReadModelRepository.save(readModel);
    }

    @KafkaListener(topics = "project-deleted", groupId = "scenario-group")
    public void handleProjectDeleted(ProjectDeletedEvent event) {
        projectReadModelRepository.deleteById(event.getProjectId());
    }

    private void updateReadModelFromEvent(ProjectReadModel readModel, Object event) {
        if (event instanceof ProjectCreatedEvent) {
            ProjectCreatedEvent createdEvent = (ProjectCreatedEvent) event;
            readModel.setProjectId(createdEvent.getProjectId());
            readModel.setProjectName(createdEvent.getProjectName());
            readModel.setLocation(createdEvent.getLocation());
            readModel.setCommodity(createdEvent.getCommodity());
            readModel.setStartDate(createdEvent.getStartDate());
            readModel.setEndDate(createdEvent.getEndDate());
            readModel.setDescription(createdEvent.getDescription());
            readModel.setProjectLead(createdEvent.getProjectLead());
            readModel.setCreatedBy(createdEvent.getCreatedBy());
            readModel.setCreatedDate(createdEvent.getCreatedDate());
        } else if (event instanceof ProjectUpdatedEvent) {
            ProjectUpdatedEvent updatedEvent = (ProjectUpdatedEvent) event;
            readModel.setProjectName(updatedEvent.getProjectName());
            readModel.setLocation(updatedEvent.getLocation());
            readModel.setCommodity(updatedEvent.getCommodity());
            readModel.setStartDate(updatedEvent.getStartDate());
            readModel.setEndDate(updatedEvent.getEndDate());
            readModel.setDescription(updatedEvent.getDescription());
            readModel.setProjectLead(updatedEvent.getProjectLead());
            readModel.setModifiedBy(updatedEvent.getModifiedBy());
            readModel.setModifiedDate(updatedEvent.getModifiedDate());
        }
    }
}
