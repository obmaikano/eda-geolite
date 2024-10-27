package com.geolite.scenarios.event;


import com.geolite.scenarios.model.ScenarioReadModel;
import com.geolite.scenarios.repository.ScenarioReadModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ScenarioEventHandler {

    @Autowired
    private ScenarioReadModelRepository scenarioReadModelRepository;

    @KafkaListener(topics = "scenario-created", groupId = "scenario-group")
    public void handleScenarioCreated(ScenarioCreatedEvent event) {
        ScenarioReadModel readModel = new ScenarioReadModel();
        // Map event data to read model
        readModel.setScenarioId(event.getScenarioId());
        readModel.setScenarioName(event.getScenarioName());
        readModel.setCreatedBy(event.getCreatedBy());
        readModel.setCreatedDate(event.getCreatedDate());
        readModel.setBudget(event.getBudget());
        readModel.setMethod(event.getMethods());
        readModel.setProjectId(event.getProjectId());
        readModel.setTarget(event.getTarget());
        scenarioReadModelRepository.save(readModel);
    }

    @KafkaListener(topics = "scenario-updated", groupId = "scenario-group")
    public void handleScenarioUpdated(ScenarioUpdatedEvent event) {
        ScenarioReadModel readModel = scenarioReadModelRepository.findById(event.getScenarioId())
                .orElseThrow(() -> new RuntimeException("Scenario not found in read model"));
        // Update read model with event data
        readModel.setScenarioName(event.getScenarioName());
        readModel.setModifiedBy(event.getModifiedBy());
        readModel.setModifiedDate(event.getModifiedDate());
        readModel.setTarget(event.getTarget());
        readModel.setScenarioId(event.getScenarioId());
        readModel.setMethod(event.getMethods());
        readModel.setBudget(event.getBudget());
        scenarioReadModelRepository.save(readModel);
    }

    @KafkaListener(topics = "scenario-deleted", groupId = "scenario-group")
    public void handleScenarioDeleted(ScenarioDeletedEvent event) {
        scenarioReadModelRepository.deleteById(event.getScenarioId());
    }
}
