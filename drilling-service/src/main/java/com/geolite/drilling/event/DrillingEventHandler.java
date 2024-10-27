package com.geolite.drilling.event;

import com.geolite.drilling.model.DrillingReadModel;
import com.geolite.drilling.repository.DrillingReadModelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DrillingEventHandler {

    @Autowired
    private DrillingReadModelRepository drillingReadModelRepository;

    @KafkaListener(topics = "drilling-started", groupId = "drilling-group")
    public void handleDrillingStarted(DrillingStartedEvent event) {
        DrillingReadModel readModel = new DrillingReadModel();
        updateReadModelFromEvent(readModel, event);
        log.info("Drilling started by: {}", event.getCreatedBy());
        drillingReadModelRepository.save(readModel);
    }

    @KafkaListener(topics = "drilling-completed", groupId = "drilling-group")
    public void handleDrillingCompleted(DrillingCompletedEvent event) {
        DrillingReadModel readModel = drillingReadModelRepository.findById(event.getHoleId())
                .orElseThrow(() -> new RuntimeException("Drilling not found in read model"));
        updateReadModelFromEvent(readModel, event);
        log.info("Drilling completed by: {}", event.getCompletedBy());
        drillingReadModelRepository.save(readModel);
    }

    @KafkaListener(topics = "drilling-deleted", groupId = "drilling-group")
    public void handleDrillingDeleted(DrillingDeletedEvent event) {
        drillingReadModelRepository.deleteById(event.getHoleId());
    }

    @KafkaListener(topics = "drilling-updated", groupId = "drilling-group")
    public void handleDrillingUpdated(DrillingUpdatedEvent event) {
        DrillingReadModel readModel = drillingReadModelRepository.findById(event.getHoleId())
                .orElseThrow(() -> new RuntimeException("Drilling not found in read model"));
        updateReadModelFromEvent(readModel, event);
        log.info("Drilling updated by: {}", event.getModifiedBy());
        drillingReadModelRepository.save(readModel);
    }

    public void updateReadModelFromEvent(DrillingReadModel readModel, Object event) {
        if (event instanceof DrillingStartedEvent) {
            DrillingStartedEvent startedEvent = (DrillingStartedEvent) event;
            readModel.setHoleId(startedEvent.getHoleId());
            readModel.setProjectId(startedEvent.getProjectId());
            readModel.setScenarioId(startedEvent.getScenarioId());
            readModel.setCollarNorthing(startedEvent.getCollarNorthing());
            readModel.setCollarEasting(startedEvent.getCollarEasting());
            readModel.setDepth(startedEvent.getDepth());
            readModel.setCreatedBy(startedEvent.getCreatedBy());
            readModel.setCreatedDate(startedEvent.getCreatedDate());
            readModel.setHoleName(startedEvent.getHoleName());
            readModel.setStatus(startedEvent.getStatus());
        } else if (event instanceof DrillingCompletedEvent) {
            DrillingCompletedEvent completedEvent = (DrillingCompletedEvent) event;
            readModel.setHoleId(completedEvent.getHoleId());
            readModel.setProjectId(completedEvent.getProjectId());
            readModel.setScenarioId(completedEvent.getScenarioId());
            readModel.setHoleName(completedEvent.getHoleName());
            readModel.setStatus(completedEvent.getStatus());
            readModel.setCompletedBy(completedEvent.getCompletedBy());
            readModel.setFinalDepth(completedEvent.getFinalDepth());
            readModel.setCompletionTime(completedEvent.getCompletionTime());
        } else if (event instanceof DrillingUpdatedEvent) {
            DrillingUpdatedEvent updatedEvent = (DrillingUpdatedEvent) event;
            readModel.setHoleId(updatedEvent.getHoleId());
            readModel.setCollarNorthing(updatedEvent.getCollarNorthing());
            readModel.setCollarEasting(updatedEvent.getCollarEasting());
            readModel.setDepth(updatedEvent.getDepth());
            readModel.setProjectId(updatedEvent.getProjectId());
            readModel.setScenarioId(updatedEvent.getScenarioId());
            readModel.setHoleName(updatedEvent.getHoleName());
            readModel.setStatus(updatedEvent.getStatus());
            readModel.setModifiedBy(updatedEvent.getModifiedBy());
            readModel.setModifiedDate(updatedEvent.getModifiedDate());
        }
    }
}
