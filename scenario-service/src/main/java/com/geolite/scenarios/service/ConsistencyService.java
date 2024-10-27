package com.geolite.scenarios.service;

import com.geolite.scenarios.event.ScenarioCreatedEvent;
import com.geolite.scenarios.event.ScenarioUpdatedEvent;
import com.geolite.scenarios.event.ScenarioDeletedEvent;
import com.geolite.scenarios.model.Scenario;
import com.geolite.scenarios.repository.ScenarioRepository;
import com.geolite.scenarios.util.ConversionUtil;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsistencyService {

    @Autowired
    private ScenarioRepository scenarioRepository;

    @KafkaListener(topics = {"scenario-created", "scenario-updated", "scenario-deleted"}, groupId = "consistency-group")
    public void ensureConsistency(ConsumerRecord<String, Object> record) {
        String topic = record.topic();
        Object value = record.value();

        switch (topic) {
            case "scenario-created":
                handleScenarioCreated((ScenarioCreatedEvent) value);
                break;
            case "scenario-updated":
                handleScenarioUpdated((ScenarioUpdatedEvent) value);
                break;
            case "scenario-deleted":
                handleScenarioDeleted((ScenarioDeletedEvent) value);
                break;
            default:
                throw new IllegalArgumentException("Unknown topic: " + topic);
        }
    }

    private void handleScenarioCreated(ScenarioCreatedEvent event) {
        Scenario scenario = new Scenario();
        updateScenarioFromEvent(scenario, event);
        scenario.setScenarioId(UUID.fromString(event.getScenarioId().toString()));
        scenarioRepository.save(scenario);
    }

    private void handleScenarioUpdated(ScenarioUpdatedEvent event) {
        scenarioRepository.findById(UUID.fromString(event.getScenarioId().toString()))
                .ifPresent(scenario -> {
                    updateScenarioFromEvent(scenario, event);
                    scenarioRepository.save(scenario);
                });
    }

    private void handleScenarioDeleted(ScenarioDeletedEvent event) {
        scenarioRepository.deleteById(UUID.fromString(event.getScenarioId().toString()));
    }

    private void updateScenarioFromEvent(Scenario scenario, ScenarioCreatedEvent event) {
        scenario.setScenarioId(UUID.fromString(event.getScenarioId().toString()));
        scenario.setProjectId(UUID.fromString(event.getProjectId().toString()));
        scenario.setScenarioName(event.getScenarioName().toString());
        scenario.setTarget(event.getTarget().toString());
        scenario.setMethods(event.getMethods().toString());
        scenario.setBudget(ConversionUtil.byteBufferToBigDecimal(event.getBudget()));
        scenario.setCreatedBy(event.getCreatedBy().toString());
        scenario.setCreatedDate(ConversionUtil.longToTimestamp(event.getCreatedDate()));
    }

    private void updateScenarioFromEvent(Scenario scenario, ScenarioUpdatedEvent event) {
        // Update only the fields that are present in the UpdatedEvent
        if (event.getScenarioName() != null) scenario.setScenarioName(event.getScenarioName().toString());
        if (event.getTarget() != null) scenario.setTarget(event.getTarget().toString());
        if (event.getMethods() != null) scenario.setMethods(event.getMethods().toString());
        if (event.getBudget() != null) scenario.setBudget(ConversionUtil.byteBufferToBigDecimal(event.getBudget()));
        scenario.setModifiedBy(event.getModifiedBy().toString());
        scenario.setModifiedDate(ConversionUtil.longToTimestamp(event.getModifiedDate()));
    }
}