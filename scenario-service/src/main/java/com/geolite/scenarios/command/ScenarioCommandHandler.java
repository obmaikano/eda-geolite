package com.geolite.scenarios.command;

import com.geolite.scenarios.event.*;
import com.geolite.scenarios.model.Scenario;
import com.geolite.scenarios.repository.ProjectReadModelRepository;
import com.geolite.scenarios.repository.ScenarioRepository;
import com.geolite.scenarios.eventstore.EventStore;
import com.geolite.scenarios.util.ConversionUtil;
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
        UUID eventId = UUID.randomUUID();
        ScenarioCreatedEvent event = new ScenarioCreatedEvent(
                eventId.toString(),
                Timestamp.from(Instant.now()).getTime(),
                scenarioId.toString(),
                command.getProjectId().toString(),
                command.getScenarioName(),
                command.getTarget(),
                command.getMethods(),
                ConversionUtil.bigDecimalToByteBuffer(command.getBudget()),
                command.getCreatedBy(),
                Timestamp.from(Instant.now()).getTime()
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

        UUID eventId = UUID.randomUUID();
        ScenarioUpdatedEvent event = new ScenarioUpdatedEvent(
                eventId.toString(),
                Timestamp.from(Instant.now()).getTime(),
                command.getScenarioId().toString(),
                command.getScenarioName(),
                command.getTarget(),
                command.getMethods(),
                ConversionUtil.bigDecimalToByteBuffer(command.getBudget()),
                command.getModifiedBy(),
                Timestamp.from(Instant.now()).getTime()
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
        ScenarioDeletedEvent event = new ScenarioDeletedEvent(UUID.randomUUID().toString(), Timestamp.from(Instant.now()).getTime(), scenario.getScenarioId().toString());

        eventStore.saveEvent(event);
        publishEvent("scenario-deleted", event);

        scenarioRepository.deleteById(command.getScenarioId());
    }

    private void applyEvent(Scenario scenario, Object event) {
        if (event instanceof ScenarioCreatedEvent createdEvent) {
            scenario.setProjectId(UUID.fromString(createdEvent.getProjectId().toString()));
            scenario.setScenarioName(createdEvent.getScenarioName().toString());
            scenario.setTarget(createdEvent.getTarget().toString());
            scenario.setMethods(createdEvent.getMethods().toString());
            scenario.setBudget(ConversionUtil.byteBufferToBigDecimal(createdEvent.getBudget()));
            scenario.setCreatedBy(createdEvent.getCreatedBy().toString());
            scenario.setCreatedDate(ConversionUtil.longToTimestamp(createdEvent.getCreatedDate()));
        } else if (event instanceof ScenarioUpdatedEvent updatedEvent) {
            scenario.setScenarioId(UUID.fromString(updatedEvent.getScenarioId().toString()));
            scenario.setScenarioName(updatedEvent.getScenarioName().toString());
            scenario.setTarget(updatedEvent.getTarget().toString());
            scenario.setMethods(updatedEvent.getMethods().toString());
            scenario.setBudget(ConversionUtil.byteBufferToBigDecimal(updatedEvent.getBudget()));
            scenario.setModifiedBy(updatedEvent.getModifiedBy().toString());
            scenario.setModifiedDate(ConversionUtil.longToTimestamp(updatedEvent.getModifiedDate()));
        }
    }

    private void publishEvent(String topic, Object event) {
        log.info("Publishing event: {}", event.toString());
        kafkaTemplate.send(topic, event);
    }
}