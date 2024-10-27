package com.geolite.drilling.listener;

import com.geolite.drilling.event.DrillingCompletedEvent;
import com.geolite.drilling.event.DrillingStartedEvent;
import com.geolite.drilling.event.DrillingUpdatedEvent;
import com.geolite.drilling.event.Event;
import com.geolite.drilling.eventstore.EventStore;
import com.geolite.drilling.model.Drilling;
import com.geolite.drilling.model.Status;
import com.geolite.drilling.repository.DrillingRepository;
import com.geolite.drilling.service.EventProcessingTracker;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@Component
public class DrillingEventListener {

    @Autowired
    private EventStore eventStore;

    @Autowired
    private DrillingRepository drillingRepository;

    @Autowired
    private EventProcessingTracker eventProcessingTracker;

    @KafkaListener(topics = "drilling-started", groupId = "drilling-group")
    @Retryable(maxAttempts = 3, value = Exception.class)
    @CircuitBreaker(name = "drillingStarted", fallbackMethod = "fallbackDrillingStarted")
    public void handleDrillingCreated(DrillingStartedEvent event) {
        if (eventProcessingTracker.isEventProcessed(event.getEventId())) {
            return; // Event already processed, ensure idempotency
        }
        eventStore.saveEvent(event);
        Drilling drilling = new Drilling();
        applyEvent(drilling, event);
        log.info("Drilling started by: {}", event.getCreatedBy());
        drillingRepository.save(drilling);
        eventProcessingTracker.markEventAsProcessed(event.getEventId());
    }

    @KafkaListener(topics = "drilling-updated", groupId = "drilling-group")
    @Retryable(maxAttempts = 3, value = Exception.class)
    @CircuitBreaker(name = "drillingUpdated", fallbackMethod = "fallbackDrillingUpdated")
    public void handleUpdateDrilling(DrillingStartedEvent event) {
        if (eventProcessingTracker.isEventProcessed(event.getEventId())) {
            return; // Event already processed, ensure idempotency
        }
        eventStore.saveEvent(event);
        Drilling drilling = drillingRepository.findById(event.getHoleId())
                .orElseThrow(() -> new RuntimeException("Drilling Hole not found"));
        applyEvent(drilling, event);
        drillingRepository.save(drilling);
        eventProcessingTracker.markEventAsProcessed(event.getEventId());
    }

    @KafkaListener(topics = "drilling-deleted", groupId = "drilling-group")
    @Retryable(maxAttempts = 3, value = Exception.class)
    @CircuitBreaker(name = "drillingDeleted", fallbackMethod = "fallbackDrillingDeleted")
    public void handleDrillingDeleted(DrillingStartedEvent event) {
        if (eventProcessingTracker.isEventProcessed(event.getEventId())) {
            return; // Event already processed, ensure idempotency
        }
        eventStore.saveEvent(event);
        Drilling drilling = drillingRepository.findById(event.getHoleId())
                .orElseThrow(() -> new RuntimeException("Drilling Hole not found"));
        applyEvent(drilling, event);
        drillingRepository.save(drilling);
        eventProcessingTracker.markEventAsProcessed(event.getEventId());
    }

    @KafkaListener(topics = "drilling-completed", groupId = "drilling-group")
    @Retryable(maxAttempts = 3, value = Exception.class)
    @CircuitBreaker(name = "drillingCompleted", fallbackMethod = "fallbackDrillingCompleted")
    public void handleDrillingCompleted(DrillingCompletedEvent event) {
        if (eventProcessingTracker.isEventProcessed(event.getEventId())) {
            return; // Event already processed, ensure idempotency
        }
        eventStore.saveEvent(event);
        Drilling drilling = drillingRepository.findById(event.getHoleId())
                .orElseThrow(() -> new RuntimeException("Drilling Hole not found"));
        applyEvent(drilling, event);
        drillingRepository.save(drilling);
        log.warn("Drilling completed by: {}", event.getCompletedBy());
        eventProcessingTracker.markEventAsProcessed(event.getEventId());
    }

    private void applyEvent(Drilling drilling, Event event) {
        if (event instanceof DrillingStartedEvent) {
            DrillingStartedEvent startedEvent = (DrillingStartedEvent) event;
            drilling.setHoleId(startedEvent.getHoleId());
            drilling.setHoleName(startedEvent.getHoleName());
            drilling.setScenarioId(startedEvent.getScenarioId());
            drilling.setProjectId(startedEvent.getProjectId());
            drilling.setCollarEasting(startedEvent.getCollarEasting());
            drilling.setCollarNorthing(startedEvent.getCollarNorthing());
            drilling.setDepth(startedEvent.getDepth());
            drilling.setCreatedBy(startedEvent.getCreatedBy());
            drilling.setCreatedDate(Timestamp.from(Instant.now()));
            drilling.setStatus(Status.STARTED);
        } else if (event instanceof DrillingUpdatedEvent) {
            DrillingUpdatedEvent drillingUpdatedEvent = (DrillingUpdatedEvent) event;
            drilling.setHoleId(drillingUpdatedEvent.getHoleId());
            drilling.setProjectId(drillingUpdatedEvent.getProjectId());
            drilling.setScenarioId(drillingUpdatedEvent.getScenarioId());
            drilling.setHoleName(drillingUpdatedEvent.getHoleName());
            drilling.setCollarEasting(drillingUpdatedEvent.getCollarEasting());
            drilling.setCollarNorthing(drillingUpdatedEvent.getCollarEasting());
            drilling.setStatus(drillingUpdatedEvent.getStatus());
            drilling.setModifiedBy(drillingUpdatedEvent.getModifiedBy());
            drilling.setModifiedDate(drillingUpdatedEvent.getModifiedDate());
        } else if (event instanceof DrillingCompletedEvent) {
            DrillingCompletedEvent completedEvent = (DrillingCompletedEvent) event;
            drilling.setHoleId(completedEvent.getHoleId());
            drilling.setProjectId(completedEvent.getProjectId());
            drilling.setScenarioId(completedEvent.getScenarioId());
            drilling.setHoleName(completedEvent.getHoleName());
            drilling.setStatus(completedEvent.getStatus());
            drilling.setFinalDepth(completedEvent.getFinalDepth());
            drilling.setCompletionTime(completedEvent.getCompletionTime());
            drilling.setCompletedBy(completedEvent.getCompletedBy());
            drilling.setStatus(Status.COMPLETED);
        }
    }
}
