package com.geolite.projects.event;

import com.geolite.projects.model.ProjectReadModel;
import com.geolite.projects.model.Status;
import com.geolite.projects.repository.ProjectReadModelRepository;
import com.geolite.projects.util.ConversionUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProjectEventHandler {

    @Autowired
    private ProjectReadModelRepository projectReadModelRepository;

    @KafkaListener(topics = "project-created", groupId = "project-group")
    public void handleProjectCreated(ProjectCreatedEvent event) {
        log.info("Project created : {}", event.toString());
        ProjectReadModel readModel = new ProjectReadModel();
        readModel.setProjectId(ConversionUtil.stringToUUID(event.getProjectId().toString()));
        readModel.setProjectName(event.getProjectName().toString());
        readModel.setLocation(event.getLocation().toString());
        readModel.setCommodity(event.getCommodity().toString());
        readModel.setStartDate(ConversionUtil.longToDate(event.getStartDate()));
        readModel.setEndDate(ConversionUtil.longToDate(event.getEndDate()));
        readModel.setDescription(event.getDescription().toString());
        readModel.setProjectLead(event.getProjectLead().toString());
        readModel.setCreatedBy(event.getCreatedBy().toString());
        readModel.setCreatedDate(ConversionUtil.longToTimestamp(event.getCreatedDate()));
        projectReadModelRepository.save(readModel);
    }

    @KafkaListener(topics = "project-updated", groupId = "project-group")
    public void handleProjectUpdated(ProjectUpdatedEvent event) {
        ProjectReadModel readModel = projectReadModelRepository.findById(ConversionUtil.stringToUUID(event.getProjectId().toString()))
                .orElseThrow(() -> new RuntimeException("Project not found in read model"));
        readModel.setProjectName(event.getProjectName().toString());
        readModel.setLocation(event.getLocation().toString());
        readModel.setCommodity(event.getCommodity().toString());
        readModel.setStartDate(ConversionUtil.longToDate(event.getStartDate()));
        readModel.setEndDate(ConversionUtil.longToDate(event.getEndDate()));
        readModel.setDescription(event.getDescription().toString());
        readModel.setProjectLead(event.getProjectLead().toString());
        readModel.setStatus(Status.fromString(event.getStatus().toString()));
        readModel.setModifiedBy(event.getModifiedBy().toString());
        readModel.setModifiedDate(ConversionUtil.longToTimestamp(event.getModifiedDate()));
        projectReadModelRepository.save(readModel);
    }

    @KafkaListener(topics = "project-deleted", groupId = "project-group")
    public void handleScenarioDeleted(ProjectDeletedEvent event) {
        projectReadModelRepository.deleteById(ConversionUtil.stringToUUID(event.getProjectId().toString()));
    }
}
