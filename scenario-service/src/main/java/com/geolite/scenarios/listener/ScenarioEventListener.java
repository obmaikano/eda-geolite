package com.geolite.scenarios.listener;

import com.geolite.scenarios.event.*;
import com.geolite.scenarios.eventstore.EventStore;
import com.geolite.scenarios.model.Scenario;
import com.geolite.scenarios.repository.ScenarioRepository;
import com.geolite.scenarios.service.EventProcessingTracker;
import com.geolite.scenarios.util.ConversionUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class ScenarioEventListener {

    @Autowired
    private EventStore eventStore;

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private EventProcessingTracker eventProcessingTracker;

    @KafkaListener(topics = "scenario-created", groupId = "scenario-group")
    @Retryable(maxAttempts = 3, value = Exception.class)
    @CircuitBreaker(name = "scenarioCreated", fallbackMethod = "fallbackScenarioCreated")
    public void handleScenarioCreated(ScenarioCreatedEvent event) {
        if (eventProcessingTracker.isEventProcessed(UUID.fromString(event.getEventId().toString()))) {
            return; // Event already processed, ensure idempotency
        }
        eventStore.saveEvent(event);
        Scenario scenario = new Scenario();
        applyEvent(scenario, event);
        log.warn("Scenario created by: {}", event.getCreatedBy());
        scenarioRepository.save(scenario);
        eventProcessingTracker.markEventAsProcessed(UUID.fromString(event.getEventId().toString()));
    }

    @KafkaListener(topics = "scenario-updated", groupId = "scenario-group")
    @Retryable(maxAttempts = 3, value = Exception.class)
    @CircuitBreaker(name = "scenarioUpdated", fallbackMethod = "fallbackScenarioUpdated")
    public void handleScenarioUpdated(ScenarioUpdatedEvent event) {
        if (eventProcessingTracker.isEventProcessed(UUID.fromString(event.getEventId().toString()))) {
            return; // Event already processed, ensure idempotency
        }
        eventStore.saveEvent(event);
        Scenario scenario = scenarioRepository.findById(UUID.fromString(event.getScenarioId().toString()))
                .orElseThrow(() -> new RuntimeException("Scenario not found"));
        applyEvent(scenario, event);
        scenarioRepository.save(scenario);
        eventProcessingTracker.markEventAsProcessed(UUID.fromString(event.getEventId().toString()));
    }

    @KafkaListener(topics = "scenario-deleted", groupId = "scenario-group")
    @Retryable(maxAttempts = 3, value = Exception.class)
    @CircuitBreaker(name = "scenarioDeleted", fallbackMethod = "fallbackScenarioDeleted")
    public void handleScenarioDeleted(ScenarioDeletedEvent event) {
        if (eventProcessingTracker.isEventProcessed(UUID.fromString(event.getEventId().toString()))) {
            return; // Event already processed, ensure idempotency
        }
        eventStore.saveEvent(event);
        scenarioRepository.deleteById(UUID.fromString(event.getScenarioId().toString()));
        eventProcessingTracker.markEventAsProcessed(UUID.fromString(event.getEventId().toString()));
    }


    public Scenario reconstructScenario(UUID scenarioId) {
        List<Object> events = eventStore.getEventsForScenario(scenarioId);
        Scenario scenario = new Scenario();
        scenario.setScenarioId(scenarioId);
        for (Object event : events) {
            applyEvent(scenario, event);
        }
        return scenario;
    }

    private void applyEvent(Scenario scenario, Object event) {
        if (event instanceof ScenarioCreatedEvent) {
            ScenarioCreatedEvent createdEvent = (ScenarioCreatedEvent) event;
            scenario.setScenarioId(UUID.fromString(createdEvent.getScenarioId().toString()));
            scenario.setProjectId(UUID.fromString(createdEvent.getProjectId().toString()));
            scenario.setScenarioName(createdEvent.getScenarioName().toString());
            scenario.setTarget(createdEvent.getTarget().toString());
            scenario.setMethods(createdEvent.getMethods().toString());
            scenario.setBudget(BigDecimal.valueOf(createdEvent.getBudget().get()));
            scenario.setCreatedBy(createdEvent.getCreatedBy().toString());
            scenario.setCreatedDate(ConversionUtil.longToTimestamp(createdEvent.getCreatedDate()));
        } else if (event instanceof ScenarioUpdatedEvent) {
            ScenarioUpdatedEvent updatedEvent = (ScenarioUpdatedEvent) event;
            scenario.setScenarioId(UUID.fromString(updatedEvent.getScenarioId().toString()));
            scenario.setScenarioName(updatedEvent.getScenarioName().toString());
            scenario.setTarget(updatedEvent.getTarget().toString());
            scenario.setMethods(updatedEvent.getMethods().toString());
            scenario.setBudget(BigDecimal.valueOf(updatedEvent.getBudget().get()));
            scenario.setModifiedBy(updatedEvent.getModifiedBy().toString());
        }
        // Note: We don't need to handle ScenarioDeletedEvent here as it doesn't modify the scenario state
    }
}
