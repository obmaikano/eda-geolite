package com.geolite.scenarios.listener;

import com.geolite.scenarios.event.*;
import com.geolite.scenarios.eventstore.EventStore;
import com.geolite.scenarios.model.Scenario;
import com.geolite.scenarios.repository.ScenarioRepository;
import com.geolite.scenarios.service.EventProcessingTracker;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

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
        if (eventProcessingTracker.isEventProcessed(event.getEventId())) {
            return; // Event already processed, ensure idempotency
        }
        eventStore.saveEvent(event);
        Scenario scenario = new Scenario();
        applyEvent(scenario, event);
        log.warn("Scenario created by: {}", event.getCreatedBy());
        scenarioRepository.save(scenario);
        eventProcessingTracker.markEventAsProcessed(event.getEventId());
    }

    @KafkaListener(topics = "scenario-updated", groupId = "scenario-group")
    @Retryable(maxAttempts = 3, value = Exception.class)
    @CircuitBreaker(name = "scenarioUpdated", fallbackMethod = "fallbackScenarioUpdated")
    public void handleScenarioUpdated(ScenarioUpdatedEvent event) {
        if (eventProcessingTracker.isEventProcessed(event.getEventId())) {
            return; // Event already processed, ensure idempotency
        }
        eventStore.saveEvent(event);
        Scenario scenario = scenarioRepository.findById(event.getScenarioId())
                .orElseThrow(() -> new RuntimeException("Scenario not found"));
        applyEvent(scenario, event);
        scenarioRepository.save(scenario);
        eventProcessingTracker.markEventAsProcessed(event.getEventId());
    }

    @KafkaListener(topics = "scenario-deleted", groupId = "scenario-group")
    @Retryable(maxAttempts = 3, value = Exception.class)
    @CircuitBreaker(name = "scenarioDeleted", fallbackMethod = "fallbackScenarioDeleted")
    public void handleScenarioDeleted(ScenarioDeletedEvent event) {
        if (eventProcessingTracker.isEventProcessed(event.getEventId())) {
            return; // Event already processed, ensure idempotency
        }
        eventStore.saveEvent(event);
        scenarioRepository.deleteById(event.getScenarioId());
        eventProcessingTracker.markEventAsProcessed(event.getEventId());
    }


    public Scenario reconstructScenario(UUID scenarioId) {
        List<Event> events = eventStore.getEventsForScenario(scenarioId);
        Scenario scenario = new Scenario();
        scenario.setScenarioId(scenarioId);
        for (Event event : events) {
            applyEvent(scenario, event);
        }
        return scenario;
    }

    private void applyEvent(Scenario scenario, Event event) {
        if (event instanceof ScenarioCreatedEvent) {
            ScenarioCreatedEvent createdEvent = (ScenarioCreatedEvent) event;
            scenario.setScenarioId(createdEvent.getScenarioId());
            scenario.setProjectId(createdEvent.getProjectId());
            scenario.setScenarioName(createdEvent.getScenarioName());
            scenario.setTarget(createdEvent.getTarget());
            scenario.setMethods(createdEvent.getMethods());
            scenario.setBudget(createdEvent.getBudget());
            scenario.setCreatedBy(createdEvent.getCreatedBy());
            scenario.setCreatedDate(createdEvent.getCreatedDate());
        } else if (event instanceof ScenarioUpdatedEvent) {
            ScenarioUpdatedEvent updatedEvent = (ScenarioUpdatedEvent) event;
            scenario.setScenarioId(updatedEvent.getScenarioId());
            scenario.setScenarioName(updatedEvent.getScenarioName());
            scenario.setTarget(updatedEvent.getTarget());
            scenario.setMethods(updatedEvent.getMethods());
            scenario.setBudget(updatedEvent.getBudget());
            scenario.setModifiedBy(updatedEvent.getModifiedBy());
        }
        // Note: We don't need to handle ScenarioDeletedEvent here as it doesn't modify the scenario state
    }
}
