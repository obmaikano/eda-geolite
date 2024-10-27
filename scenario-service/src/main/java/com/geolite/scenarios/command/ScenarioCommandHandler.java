package com.geolite.scenarios.command;

import com.geolite.scenarios.event.*;
import com.geolite.scenarios.model.Scenario;
import com.geolite.scenarios.repository.ProjectReadModelRepository;
import com.geolite.scenarios.repository.ScenarioRepository;
import com.geolite.scenarios.eventstore.EventStore;
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
public class ScenarioCommandHandler {

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private EventStore eventStore;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ProjectReadModelRepository projectReadModelRepository;

    @Transactional
    public Scenario handleCreateScenario(CreateScenarioCommand command) {

        Objects.requireNonNull(command.getScenarioName(), "Scenario name is required");
        Objects.requireNonNull(command.getProjectId(), "Project is required");
        Objects.requireNonNull(command.getTarget(), "Target is required");
        Objects.requireNonNull(command.getMethods(), "Methods are required");
        Objects.requireNonNull(command.getBudget(), "Budget is required");
        Objects.requireNonNull(command.getCreatedBy(), "Created by is required");

        // Check if project exists
        if (!projectReadModelRepository.existsById(command.getProjectId())) {
            throw new IllegalArgumentException("Project does not exist");
        }

        UUID scenarioId = UUID.randomUUID();
        ScenarioCreatedEvent event = new ScenarioCreatedEvent(
                scenarioId,
                command.getProjectId(),
                command.getScenarioName(),
                command.getTarget(),
                command.getMethods(),
                command.getBudget(),
                command.getCreatedBy(),
                Timestamp.from(Instant.now())
        );

        eventStore.saveEvent(event);
        publishEvent("scenario-created", event);

        Scenario scenario = new Scenario();
        scenario.setScenarioId(scenarioId);
        applyEvent(scenario, event);
        log.warn("Scenario created at: {}", scenario.getCreatedDate());
        return  scenarioRepository.save(scenario);
    }

    @Transactional
    public void handleUpdateScenario(UpdateScenarioCommand command) {
        Scenario scenario = scenarioRepository.findById(command.getScenarioId())
                .orElseThrow(() -> new RuntimeException("Scenario not found"));

        Objects.requireNonNull(command.getScenarioName(), "Scenario name is required");
        Objects.requireNonNull(command.getTarget(), "Target is required");
        Objects.requireNonNull(command.getMethods(), "Methods are required");
        Objects.requireNonNull(command.getBudget(), "Budget is required");
        Objects.requireNonNull(command.getModifiedBy(), "Modified by is required");

        ScenarioUpdatedEvent event = new ScenarioUpdatedEvent(
                command.getScenarioId(),
                command.getScenarioName(),
                command.getTarget(),
                command.getMethods(),
                command.getBudget(),
                command.getModifiedBy(),
                Timestamp.from(Instant.now())
        );

        eventStore.saveEvent(event);
        publishEvent("scenario-updated", event);
        applyEvent(scenario, event);
        scenarioRepository.save(scenario);
    }

    @Transactional
    public void handleDeleteScenario(DeleteScenarioCommand command) {
        Scenario scenario = scenarioRepository.findById(command.getScenarioId())
                .orElseThrow(() -> new RuntimeException("Scenario not found"));
        ScenarioDeletedEvent event = new ScenarioDeletedEvent(scenario.getScenarioId());

        eventStore.saveEvent(event);
        publishEvent("scenario-deleted", event);

        scenarioRepository.deleteById(command.getScenarioId());
    }

    private void applyEvent(Scenario scenario, Event event) {
        if (event instanceof ScenarioCreatedEvent createdEvent) {
            scenario.setProjectId(createdEvent.getProjectId());
            scenario.setScenarioName(createdEvent.getScenarioName());
            scenario.setTarget(createdEvent.getTarget());
            scenario.setMethods(createdEvent.getMethods());
            scenario.setBudget(createdEvent.getBudget());
            scenario.setCreatedBy(createdEvent.getCreatedBy());
            scenario.setCreatedDate(createdEvent.getCreatedDate());
        } else if (event instanceof ScenarioUpdatedEvent updatedEvent) {
            scenario.setScenarioId(updatedEvent.getScenarioId());
            scenario.setScenarioName(updatedEvent.getScenarioName());
            scenario.setTarget(updatedEvent.getTarget());
            scenario.setMethods(updatedEvent.getMethods());
            scenario.setBudget(updatedEvent.getBudget());
            scenario.setModifiedBy(updatedEvent.getModifiedBy());
            scenario.setModifiedDate(updatedEvent.getModifiedDate());
        }
    }

    private void publishEvent(String topic, Event event) {
        kafkaTemplate.send(topic, event);
    }
}