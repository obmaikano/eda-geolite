package com.geolite.scenarios.service;

import com.geolite.scenarios.event.ScenarioCreatedEvent;
import com.geolite.scenarios.event.ScenarioUpdatedEvent;
import com.geolite.scenarios.event.ScenarioDeletedEvent;
import com.geolite.scenarios.model.Scenario;
import com.geolite.scenarios.repository.ScenarioRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
        scenario.setScenarioId(event.getScenarioId());
        scenarioRepository.save(scenario);
    }

    private void handleScenarioUpdated(ScenarioUpdatedEvent event) {
        scenarioRepository.findById(event.getScenarioId())
                .ifPresent(scenario -> {
                    updateScenarioFromEvent(scenario, event);
                    scenarioRepository.save(scenario);
                });
    }

    private void handleScenarioDeleted(ScenarioDeletedEvent event) {
        scenarioRepository.deleteById(event.getScenarioId());
    }

    private void updateScenarioFromEvent(Scenario scenario, ScenarioCreatedEvent event) {
        scenario.setScenarioId(event.getScenarioId());
        scenario.setProjectId(event.getProjectId());
        scenario.setScenarioName(event.getScenarioName());
        scenario.setTarget(event.getTarget());
        scenario.setMethods(event.getMethods());
        scenario.setBudget(event.getBudget());
        scenario.setCreatedBy(event.getCreatedBy());
        scenario.setCreatedDate(event.getCreatedDate());
    }

    private void updateScenarioFromEvent(Scenario scenario, ScenarioUpdatedEvent event) {
        // Update only the fields that are present in the UpdatedEvent
        if (event.getScenarioName() != null) scenario.setScenarioName(event.getScenarioName());
        if (event.getTarget() != null) scenario.setTarget(event.getTarget());
        if (event.getMethods() != null) scenario.setMethods(event.getMethods());
        if (event.getBudget() != null) scenario.setBudget(event.getBudget());
        scenario.setModifiedBy(event.getModifiedBy());
        scenario.setModifiedDate(event.getModifiedDate());
    }
}