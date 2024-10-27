package com.geolite.scenarios.event;

import com.geolite.scenarios.model.ScenarioReadModel;
import com.geolite.scenarios.repository.ScenarioReadModelRepository;
import com.geolite.scenarios.util.ConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class ScenarioEventHandler {

    @Autowired
    private ScenarioReadModelRepository scenarioReadModelRepository;

    @KafkaListener(topics = "scenario-created", groupId = "scenario-group")
    public void handleScenarioCreated(ScenarioCreatedEvent event) {
        ScenarioReadModel readModel = new ScenarioReadModel();
        // Map event data to read model
        readModel.setScenarioId(UUID.fromString(event.getScenarioId().toString()));
        readModel.setScenarioName(event.getScenarioName().toString());
        readModel.setCreatedBy(event.getCreatedBy().toString());
        readModel.setCreatedDate(ConversionUtil.longToTimestamp(event.getCreatedDate()));
        readModel.setBudget(BigDecimal.valueOf(event.getBudget().get()));
        readModel.setMethod(event.getMethods().toString());
        readModel.setProjectId(UUID.fromString(event.getProjectId().toString()));
        readModel.setTarget(event.getTarget().toString());
        scenarioReadModelRepository.save(readModel);
    }

    @KafkaListener(topics = "scenario-updated", groupId = "scenario-group")
    public void handleScenarioUpdated(ScenarioUpdatedEvent event) {
        ScenarioReadModel readModel = scenarioReadModelRepository.findById(UUID.fromString(event.getScenarioId().toString()))
                .orElseThrow(() -> new RuntimeException("Scenario not found in read model"));
        // Update read model with event data
        readModel.setScenarioName(event.getScenarioName().toString());
        readModel.setModifiedBy(event.getModifiedBy().toString());
        readModel.setModifiedDate(ConversionUtil.longToTimestamp(event.getModifiedDate()));
        readModel.setTarget(event.getTarget().toString());
        readModel.setMethod(event.getMethods().toString());
        readModel.setBudget(BigDecimal.valueOf(event.getBudget().get()));
        scenarioReadModelRepository.save(readModel);
    }

    @KafkaListener(topics = "scenario-deleted", groupId = "scenario-group")
    public void handleScenarioDeleted(ScenarioDeletedEvent event) {
        scenarioReadModelRepository.deleteById(UUID.fromString(event.getScenarioId().toString()));
    }
}
