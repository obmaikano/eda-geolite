package com.geolite.scenarios.event;

import com.geolite.projects.event.ProjectCreatedEvent;

import com.geolite.scenarios.model.ProjectReadModel;
import com.geolite.scenarios.repository.ProjectReadModelRepository;
import com.geolite.scenarios.util.ConversionUtil;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
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
       log.info("Project published by project producer: {}", event.toString());
       projectReadModelRepository.save(readModel);
   }

//    @KafkaListener(topics = "project-updated", groupId = "scenario-group")
//    public void handleProjectUpdated(ProjectUpdatedEvent event) {
//        ProjectReadModel readModel = projectReadModelRepository.findById(event.getProjectId())
//                .orElseThrow(() -> new RuntimeException("Project not found in read model"));
//        updateReadModelFromEvent(readModel, event);
//        projectReadModelRepository.save(readModel);
//    }

//    @KafkaListener(topics = "project-deleted", groupId = "scenario-group")
//    public void handleProjectDeleted(ProjectDeletedEvent event) {
//        projectReadModelRepository.deleteById(event.getProjectId());
//    }

   private void updateReadModelFromEvent(ProjectReadModel readModel, Object event) {
       if (event instanceof ProjectCreatedEvent) {
           ProjectCreatedEvent createdEvent = (ProjectCreatedEvent) event;
           readModel.setProjectId(UUID.fromString(createdEvent.getProjectId().toString()));
           readModel.setProjectName(createdEvent.getProjectName().toString());
           readModel.setLocation(createdEvent.getLocation().toString());
           readModel.setCommodity(createdEvent.getCommodity().toString());
           readModel.setStartDate(ConversionUtil.longToDate(createdEvent.getStartDate()));
           readModel.setEndDate(ConversionUtil.longToDate(createdEvent.getEndDate()));
           readModel.setDescription(createdEvent.getDescription().toString());
           readModel.setProjectLead(createdEvent.getProjectLead().toString());
           readModel.setCreatedBy(createdEvent.getCreatedBy().toString());
           readModel.setCreatedDate(ConversionUtil.longToTimestamp(createdEvent.getCreatedDate()));
       } 
    //    else if (event instanceof ProjectUpdatedEvent) {
    //        ProjectUpdatedEvent updatedEvent = (ProjectUpdatedEvent) event;
    //        readModel.setProjectName(updatedEvent.getProjectName());
    //        readModel.setLocation(updatedEvent.getLocation());
    //        readModel.setCommodity(updatedEvent.getCommodity());
    //        readModel.setStartDate(updatedEvent.getStartDate());
    //        readModel.setEndDate(updatedEvent.getEndDate());
    //        readModel.setDescription(updatedEvent.getDescription());
    //        readModel.setProjectLead(updatedEvent.getProjectLead());
    //        readModel.setModifiedBy(updatedEvent.getModifiedBy());
    //        readModel.setModifiedDate(updatedEvent.getModifiedDate());
    //    }
   }
}
