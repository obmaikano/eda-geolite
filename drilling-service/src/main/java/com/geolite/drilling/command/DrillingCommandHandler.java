package com.geolite.drilling.command;

import com.geolite.drilling.event.*;
import com.geolite.drilling.eventstore.EventStore;
import com.geolite.drilling.model.Drilling;
import com.geolite.drilling.model.Status;
import com.geolite.drilling.repository.DrillingRepository;
import com.geolite.drilling.repository.ProjectReadModelRepository;
import com.geolite.drilling.repository.ScenarioReadModelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class DrillingCommandHandler {

    @Autowired
    private DrillingRepository drillingRepository;

    @Autowired
    private EventStore eventStore;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ProjectReadModelRepository projectReadModelRepository;

    @Autowired
    private ScenarioReadModelRepository scenarioReadModelRepository;

    private void publishEvent(String topic, Event event) {
        kafkaTemplate.send(topic, event);
    }

    @Transactional
    public Drilling handleStartDrilling(StartDrillingCommand command) {

        Objects.requireNonNull(command.getProjectId(), "Project is required");
        Objects.requireNonNull(command.getScenarioId(), "Scenario is required");
        Objects.requireNonNull(command.getHoleName(), "Hole name is required");

        // check if project exists
        if (!projectReadModelRepository.existsById(command.getProjectId())) {
            throw new IllegalArgumentException("Project does not exist");
        }

        // check if scenario exists
        if (!scenarioReadModelRepository.existsById(command.getScenarioId())) {
            throw new IllegalArgumentException("Scenario does not exist");
        }

        UUID holeId = UUID.randomUUID();
        DrillingStartedEvent event = new DrillingStartedEvent(
                holeId,
                command.getProjectId(),
                command.getScenarioId(),
                command.getHoleName(),
                command.getCollarEasting(),
                command.getCollarNorthing(),
                command.getDepth(),
                Status.STARTED,
                command.getStartTime(),
                command.getCreatedBy(),
                Timestamp.from(Instant.now())
        );

        eventStore.saveEvent(event);
        publishEvent("drilling-started", event);

        Drilling drilling = new Drilling();
        applyEvent(drilling, event);
        log.warn("Drilling started at: {}", drilling.getCreatedDate());
        return drillingRepository.save(drilling);
    }

    @Transactional
    public Drilling handleUpdateDrilling(UpdateDrillingCommand command) {

        Drilling drilling = drillingRepository.findById(command.getHoleId())
                .orElseThrow(() -> new RuntimeException("Drilling Hole not found"));

        Objects.requireNonNull(command.getProjectId(), "Project is required");
        Objects.requireNonNull(command.getScenarioId(), "Scenario is required");
        Objects.requireNonNull(command.getHoleName(), "Hole name is required");

        DrillingUpdatedEvent event = new DrillingUpdatedEvent(
                command.getHoleId(),
                command.getProjectId(),
                command.getScenarioId(),
                command.getHoleName(),
                command.getCollarEasting(),
                command.getCollarNorthing(),
                command.getDepth(),
                command.getStatus(),
                command.getModifiedBy(),
                Timestamp.from(Instant.now())
        );

        eventStore.saveEvent(event);
        publishEvent("drilling-updated", event);
        applyEvent(drilling, event);
        log.warn("Drilling updated at: {}", drilling.getCreatedDate());
        return drillingRepository.save(drilling);
    }

    @Transactional
    public void handleDeleteDrilling(DeleteDrillingCommand command) {

        Drilling drilling = drillingRepository.findById(command.getHoleId())
                .orElseThrow(() -> new RuntimeException("Drilling Hole not found"));

        DrillingDeletedEvent event = new DrillingDeletedEvent(drilling.getHoleId());
        eventStore.saveEvent(event);
        publishEvent("drilling-deleted", event);
        drillingRepository.deleteById(command.getHoleId());
    }

    @Transactional
    public Drilling handleCompleteDrilling(CompleteDrillingCommand command) {
        Drilling drilling = drillingRepository.findById(command.getHoleId())
                .orElseThrow(() -> new RuntimeException("Drilling Hole not found"));

        DrillingCompletedEvent event = new DrillingCompletedEvent(
                command.getHoleId(),
                command.getHoleId(),
                command.getScenarioId(),
                command.getHoleName(),
                command.getFinalDepth(),
                command.getCompletionTime(),
                command.getCompletedBy(),
                Status.COMPLETED
        );

        eventStore.saveEvent(event);
        publishEvent("drilling-completed", event);
        applyEvent(drilling, event);
        log.warn("Drilling completed at: {}", drilling.getCreatedDate());
        return drillingRepository.save(drilling);
    }

    private void applyEvent(Drilling drilling, Event event) {
        if (event instanceof DrillingStartedEvent drillingStartedEvent) {
            drilling.setHoleId(drillingStartedEvent.getHoleId());
            drilling.setProjectId(drillingStartedEvent.getProjectId());
            drilling.setScenarioId(drillingStartedEvent.getScenarioId());
            drilling.setHoleName(drillingStartedEvent.getHoleName());
            drilling.setCollarEasting(drillingStartedEvent.getCollarEasting());
            drilling.setCollarNorthing(drillingStartedEvent.getCollarEasting());
            drilling.setStatus(drillingStartedEvent.getStatus());
            drilling.setStartTime(drillingStartedEvent.getStartTime());
            drilling.setDepth(drillingStartedEvent.getDepth());
            drilling.setCreatedBy(drillingStartedEvent.getCreatedBy());
            drilling.setCreatedDate(drillingStartedEvent.getCreatedDate());
        } else if (event instanceof DrillingUpdatedEvent drillingUpdatedEvent) {
            drilling.setHoleId(drillingUpdatedEvent.getHoleId());
            drilling.setProjectId(drillingUpdatedEvent.getProjectId());
            drilling.setScenarioId(drillingUpdatedEvent.getScenarioId());
            drilling.setHoleName(drillingUpdatedEvent.getHoleName());
            drilling.setCollarEasting(drillingUpdatedEvent.getCollarEasting());
            drilling.setCollarNorthing(drillingUpdatedEvent.getCollarEasting());
            drilling.setStatus(drillingUpdatedEvent.getStatus());
            drilling.setModifiedBy(drillingUpdatedEvent.getModifiedBy());
            drilling.setModifiedDate(drillingUpdatedEvent.getModifiedDate());
        } else if (event instanceof DrillingCompletedEvent drillingCompletedEvent) {
            drilling.setHoleId(drillingCompletedEvent.getHoleId());
            drilling.setProjectId(drillingCompletedEvent.getProjectId());
            drilling.setScenarioId(drillingCompletedEvent.getScenarioId());
            drilling.setHoleName(drillingCompletedEvent.getHoleName());
            drilling.setFinalDepth(drillingCompletedEvent.getFinalDepth());
            drilling.setCompletionTime(drillingCompletedEvent.getCompletionTime());
            drilling.setCompletedBy(drillingCompletedEvent.getCompletedBy());
            drilling.setStatus(Status.COMPLETED);
        }
    }
}
